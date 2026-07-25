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
import com.tuusuario.mileagetracker.util.calculateTotalDistance
import com.tuusuario.mileagetracker.util.filterGpsNoise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * TrackingService.kt  (NUEVO en v2.0)
 * -----------------------------------------------------------------------
 * Un "Foreground Service" es un componente especial de Android que puede
 * seguir corriendo aunque el usuario minimice la app o apague la
 * pantalla, SIEMPRE que muestre una notificación visible y persistente
 * (es la regla que exige Android para evitar apps espía en segundo
 * plano).
 *
 * Este servicio:
 *   1. Al iniciar (ACTION_START), muestra la notificación "Rastreando..."
 *      y empieza a escuchar el GPS con LocationTracker.
 *   2. Cada vez que llega un punto GPS nuevo, actualiza
 *      TrackingSessionState (que la UI está observando) y refresca el
 *      texto de la notificación con las millas actuales.
 *   3. Al recibir ACTION_STOP, detiene el GPS y se autodestruye.
 * -----------------------------------------------------------------------
 */
class TrackingService : Service() {

    companion object {
        const val ACTION_START = "com.tuusuario.mileagetracker.action.START"
        const val ACTION_STOP = "com.tuusuario.mileagetracker.action.STOP"
        private const val CHANNEL_ID = "mileage_tracking_channel"
        private const val NOTIFICATION_ID = 1001
    }

    private val serviceScope = CoroutineScope(SupervisorJob())
    private var trackingJob: Job? = null
    private lateinit var locationTracker: LocationTracker
    private val routePoints = mutableListOf<com.tuusuario.mileagetracker.util.GpsPoint>()

    override fun onCreate() {
        super.onCreate()
        locationTracker = LocationTracker(applicationContext)
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
            locationTracker.trackLocation().collect { point ->
                routePoints.add(point)
                val cleanRoute = filterGpsNoise(routePoints)
                val miles = calculateTotalDistance(cleanRoute)
                TrackingSessionState.addPoint(point, miles)
                updateNotification("${"%.2f".format(miles)} millas recorridas")
            }
        }
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null // No usamos "binding", solo Start/Stop

    // ---- Notificación persistente (obligatoria para un Foreground Service) ----

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
}
