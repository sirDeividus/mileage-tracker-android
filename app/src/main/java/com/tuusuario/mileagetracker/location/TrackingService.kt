package com.tuusuario.mileagetracker.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tuusuario.mileagetracker.MainActivity
import com.tuusuario.mileagetracker.data.local.AppDatabase
import com.tuusuario.mileagetracker.data.local.TripDao
import com.tuusuario.mileagetracker.data.local.TripEntity
import com.tuusuario.mileagetracker.data.local.UserPreferences
import com.tuusuario.mileagetracker.util.GpsPoint
import com.tuusuario.mileagetracker.util.MIN_TRACKABLE_MILES
import com.tuusuario.mileagetracker.util.calculateTotalDistance
import com.tuusuario.mileagetracker.util.filterGpsNoise
import com.tuusuario.mileagetracker.util.stringsFor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

/**
 * TrackingService.kt  (ACTUALIZADO v2.4)
 * -----------------------------------------------------------------------
 * Un "Foreground Service" es un componente especial de Android que puede
 * seguir corriendo aunque el usuario minimice la app o apague la
 * pantalla, SIEMPRE que muestre una notificación visible y persistente.
 *
 * NUEVO v2.4 — AUTOGUARDADO (para no perder millas si se te olvida cerrar
 * el viaje o si te pasas de la medianoche trabajando):
 *   1. Apenas empieza un viaje, se crea de inmediato una fila "borrador"
 *      en la base de datos (isActive = true), no solo en memoria.
 *   2. Mientras rastrea, esa fila se va ACTUALIZANDO cada ~20 segundos con
 *      las millas/ruta acumuladas — así, si la app se cierra a la fuerza,
 *      el teléfono se reinicia o el proceso muere, como mucho se pierden
 *      unos segundos de manejo, nunca el viaje completo.
 *   3. Si detecta que cambió el día (medianoche) mientras el viaje sigue
 *      activo, CIERRA automáticamente el viaje de "ayer" con las millas
 *      hasta ese momento y empieza uno nuevo para "hoy" — sin que el
 *      usuario tenga que hacer nada, y sin mezclar millas de dos días en
 *      un mismo registro.
 *   4. Si el usuario de verdad se olvida de presionar "Stop Work" por
 *      horas o días, esa fila "borrador" sigue existiendo en la base de
 *      datos con las últimas millas guardadas — HomeViewModel la detecta
 *      la próxima vez que se abre la app y la cierra automáticamente
 *      (ver HomeViewModel.recoverOrphanedTrip()).
 * -----------------------------------------------------------------------
 */
class TrackingService : Service() {

    companion object {
        const val ACTION_START = "com.tuusuario.mileagetracker.action.START"
        const val ACTION_STOP = "com.tuusuario.mileagetracker.action.STOP"
        private const val CHANNEL_ID = "mileage_tracking_channel"
        private const val NOTIFICATION_ID = 1001
        private const val DAY_SPLIT_NOTIFICATION_ID = 1002

        // Cada cuánto se actualiza el "borrador" en la base de datos mientras
        // se rastrea. No hace falta escribir en cada punto GPS (cada 5s) —
        // cada 20s es suficiente para no perder casi nada y no gastar batería.
        private const val DRAFT_SAVE_INTERVAL_MS = 20_000L
    }

    private val serviceScope = CoroutineScope(SupervisorJob())
    private var trackingJob: Job? = null
    private lateinit var locationTracker: LocationTracker
    private lateinit var tripDao: TripDao
    private val routePoints = mutableListOf<GpsPoint>()

    // Id de la fila "borrador" en la base de datos para el viaje en curso.
    private var activeTripId: Long = 0L
    // Clave de "día" (año * 1000 + día del año) del viaje activo, para
    // detectar cuándo cruza la medianoche.
    private var activeTripDayKey: Int = -1
    private var lastDraftSaveMillis: Long = 0L

    override fun onCreate() {
        super.onCreate()
        locationTracker = LocationTracker(applicationContext)
        tripDao = AppDatabase.getInstance(applicationContext).tripDao()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        // START_STICKY: si Android mata el proceso por falta de memoria,
        // intenta recrear el servicio automáticamente.
        return START_STICKY
    }

    private fun startTracking() {
        routePoints.clear()
        TrackingSessionState.begin()

        startForeground(NOTIFICATION_ID, buildNotification("0.00 millas recorridas"))

        trackingJob = serviceScope.launch {
            // Se crea la fila "borrador" ANTES de escuchar el GPS, para que
            // exista en la base de datos desde el primer segundo del viaje.
            activeTripId = tripDao.insertTrip(buildDraftEntity())
            activeTripDayKey = dayKeyFor(System.currentTimeMillis())
            lastDraftSaveMillis = System.currentTimeMillis()

            locationTracker.trackLocation().collect { point ->
                // NUEVO: si el punto GPS cae en un día distinto al que
                // empezó el viaje, cerramos el de "ayer" y abrimos uno
                // nuevo para "hoy" — todo dentro de esta misma corrutina,
                // así que no hay dos escrituras compitiendo a la vez.
                if (dayKeyFor(point.timestampMillis) != activeTripDayKey) {
                    splitTripAtMidnight()
                }

                routePoints.add(point)
                val cleanRoute = filterGpsNoise(routePoints)
                val miles = calculateTotalDistance(cleanRoute)
                TrackingSessionState.addPoint(point, miles)
                updateNotification("${"%.2f".format(miles)} millas recorridas")

                val now = System.currentTimeMillis()
                if (now - lastDraftSaveMillis >= DRAFT_SAVE_INTERVAL_MS) {
                    lastDraftSaveMillis = now
                    saveDraft(finalize = false)
                }
            }
        }
    }

    private fun stopTracking() {
        val jobToStop = trackingJob
        trackingJob = null

        serviceScope.launch {
            // Esperamos a que el coroutine de rastreo termine por completo
            // (incluida cualquier escritura de borrador en curso) ANTES de
            // hacer el guardado final, para no pisar una escritura con otra.
            jobToStop?.cancelAndJoin()

            val cleanRoute = filterGpsNoise(routePoints)
            val miles = calculateTotalDistance(cleanRoute)
            if (miles < MIN_TRACKABLE_MILES) {
                tripDao.deleteTripById(activeTripId)
            } else {
                saveDraft(finalize = true)
            }

            activeTripId = 0L
            activeTripDayKey = -1
            TrackingSessionState.reset()

            // stopForeground/stopSelf son llamadas de ciclo de vida de
            // Service — las devolvemos al hilo principal en vez de
            // dejarlas en el dispatcher de fondo donde corrió la escritura.
            withContext(Dispatchers.Main) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    /** Cierra el viaje del día que termina y abre uno nuevo para el día que empieza. */
    private suspend fun splitTripAtMidnight() {
        val cleanRoute = filterGpsNoise(routePoints)
        val miles = calculateTotalDistance(cleanRoute)
        if (miles < MIN_TRACKABLE_MILES) {
            tripDao.deleteTripById(activeTripId)
        } else {
            saveDraft(finalize = true)
        }

        // Empieza de cero para el nuevo día, pero conserva la plataforma y
        // los peajes ya elegidos (TrackingSessionState.begin() NO los borra,
        // solo reinicia la ruta/millas/hora de inicio).
        routePoints.clear()
        TrackingSessionState.begin()
        activeTripId = tripDao.insertTrip(buildDraftEntity())
        activeTripDayKey = dayKeyFor(System.currentTimeMillis())
        lastDraftSaveMillis = System.currentTimeMillis()

        notifyDaySplit()
    }

    /** Actualiza (o cierra, si finalize=true) la fila del viaje activo con las millas/ruta acumuladas. */
    private suspend fun saveDraft(finalize: Boolean) {
        if (activeTripId == 0L) return
        val cleanRoute = filterGpsNoise(routePoints)
        val miles = calculateTotalDistance(cleanRoute)
        val platformId = TrackingSessionState.selectedPlatform
        val customName = TrackingSessionState.customPlatformName
        val platformValue = if (platformId == "other") customName.trim() else platformId
        val tollAmount = TrackingSessionState.tollAmountText.toDoubleOrNull() ?: 0.0

        tripDao.updateTrip(
            TripEntity(
                id = activeTripId,
                startTimeMillis = TrackingSessionState.startTimeMillis,
                endTimeMillis = System.currentTimeMillis(),
                miles = miles,
                note = "",
                routeJson = routeToJson(cleanRoute),
                platform = platformValue,
                tollAmount = tollAmount,
                isActive = !finalize,
            )
        )
    }

    private fun buildDraftEntity(): TripEntity {
        val platformId = TrackingSessionState.selectedPlatform
        val customName = TrackingSessionState.customPlatformName
        val platformValue = if (platformId == "other") customName.trim() else platformId
        return TripEntity(
            startTimeMillis = TrackingSessionState.startTimeMillis,
            endTimeMillis = TrackingSessionState.startTimeMillis,
            miles = 0.0,
            note = "",
            routeJson = "[]",
            platform = platformValue,
            tollAmount = TrackingSessionState.tollAmountText.toDoubleOrNull() ?: 0.0,
            isActive = true,
        )
    }

    private fun dayKeyFor(millis: Long): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR)
    }

    private fun routeToJson(route: List<GpsPoint>): String {
        val array = JSONArray()
        route.forEach { point ->
            val obj = JSONObject()
            obj.put("lat", point.latitude)
            obj.put("lng", point.longitude)
            obj.put("t", point.timestampMillis)
            array.put(obj)
        }
        return array.toString()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null // No usamos "binding", solo Start/Stop

    // ---- Notificaciones ----

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rastreo de millas",
                NotificationManager.IMPORTANCE_LOW // sin sonido, no molesta
            ).apply {
                description = "Muestra el rastreo de millas de trabajo en curso"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(contentText: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Mileage Tracker — Viaje en curso")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // el usuario no puede deslizarla para cerrarla
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification(contentText: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(contentText))
    }

    /**
     * NUEVO v2.4: notificación aparte (no la persistente de rastreo) para
     * avisar que el viaje se dividió automáticamente al cambiar de día —
     * así el usuario se entera aunque tenga la app cerrada o esté dormido.
     */
    private fun notifyDaySplit() {
        val strings = stringsFor(UserPreferences(applicationContext).language)
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 1, openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(strings.daySplitNotificationTitle)
            .setContentText(strings.daySplitNotificationBody)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(DAY_SPLIT_NOTIFICATION_ID, notification)
    }
}
