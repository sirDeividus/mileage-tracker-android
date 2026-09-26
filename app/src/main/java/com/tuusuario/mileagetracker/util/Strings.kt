package com.tuusuario.mileagetracker.util

import androidx.compose.runtime.compositionLocalOf
import com.tuusuario.mileagetracker.data.local.AppLanguage

/**
 * Strings.kt  (NUEVO)
 * -----------------------------------------------------------------------
 * En vez de escribir cada texto directamente dentro de cada pantalla
 * (lo que haría imposible cambiar de idioma), centralizamos TODOS los
 * textos visibles de la app en esta clase, en español e inglés.
 *
 * AppStrings.current(language) devuelve el set de textos correcto, y
 * LocalAppStrings (un CompositionLocal) lo hace disponible en cualquier
 * pantalla de Compose sin tener que pasarlo manualmente como parámetro
 * de función en función.
 * -----------------------------------------------------------------------
 */
data class AppStrings(
    // Encabezado / saludo
    val appTitle: String,
    val greetingMorning: String,
    val greetingAfternoon: String,
    val greetingEvening: String,
    val motivationalQuotes: List<String>,

    // Home
    val homeSubtitle: String,
    val readyToStart: String,
    val trackingInProgress: String,
    val startWork: String,
    val stopWork: String,
    val thisMonth: String,
    val totalMiles: String,
    val estimatedDeduction: String,
    val platformQuestion: String,
    val customPlatformPlaceholder: String,
    val tollLabel: String,
    val tollHint: String,
    val disclaimerHome: String,

    // Tip modal
    val tipTitle: String,
    val tipBody: String,
    val tipDontShowAgain: String,
    val tipGotIt: String,
    val tripTooShortError: String,
    val daySplitNotificationTitle: String,
    val daySplitNotificationBody: String,
    val recoveredTripMessage: String,
    val tollDetectedTitle: String,
    val tollDetectedBody: String,
    val tollDetectedIgnore: String,

    // Historial
    val historyTitle: String,
    val tripsRegistered: String,
    val noTripsYet: String,
    val deleteTripTitle: String,
    val deleteTripConfirm: String,
    val cancel: String,
    val delete: String,
    val noPlatform: String,
    val irsRateLabel: String,
    val edit: String,
    val editTripTitle: String,
    val addTripTitle: String,
    val addTripFab: String,
    val manualTripExplanation: String,
    val fieldDate: String,
    val fieldMiles: String,
    val fieldNote: String,
    val save: String,
    val invalidMilesError: String,

    // Resumen
    val summaryTitle: String,
    val summarySubtitle: String,
    val periodMonth: String,
    val periodQuarter: String,
    val periodYear: String,
    val estimatedDeductionLabel: String,
    val totalMilesLabel: String,
    val tripsLabel: String,
    val totalTollsLabel: String,
    val combinedDeductionLabel: String,
    val tollExplanation: String,
    val irsRatesUsed: String,
    val aboutYourState: String,
    val chooseYourState: String,
    val summaryDisclaimer: String,
    val donateButton: String,
    val platformBreakdownTitle: String,

    // Ajustes
    val settingsTitle: String,
    val settingsLanguage: String,
    val settingsTheme: String,
    val settingsState: String,
    val detectStateButton: String,
    val detectStateDetecting: String,
    val detectStateFoundPrefix: String,
    val detectStateError: String,
    val detectStatePermissionDenied: String,
    val themeLight: String,
    val themeDark: String,
    val themeAuto: String,
    val settingsBackupSection: String,
    val backupExportButton: String,
    val backupImportButton: String,
    val backupExplanation: String,
    val backupExportSuccess: String,
    val backupImportSuccess: String,
    val backupImportError: String,

    // Pestañas de navegación
    val tabHome: String,
    val tabHistory: String,
    val tabSummary: String,
    val tabSettings: String,
)

private val SPANISH_QUOTES = listOf(
    "Cada milla cuenta — literalmente.",
    "Hoy es un buen día para ganar y ahorrar en impuestos.",
    "Un viaje a la vez, un dólar deducido a la vez.",
    "Tu esfuerzo de hoy es tu deducción de mañana.",
    "Maneja seguro, rastrea todo, deduce lo justo.",
)

private val ENGLISH_QUOTES = listOf(
    "Every mile counts — literally.",
    "Today is a good day to earn and save on taxes.",
    "One trip at a time, one dollar deducted at a time.",
    "Today's effort is tomorrow's deduction.",
    "Drive safe, track everything, deduct what's fair.",
)

private val SPANISH = AppStrings(
    appTitle = "Mileage Tracker",
    greetingMorning = "Buenos días",
    greetingAfternoon = "Buenas tardes",
    greetingEvening = "Buenas noches",
    motivationalQuotes = SPANISH_QUOTES,

    homeSubtitle = "Rastrea tus millas de trabajo en cualquier estado de EE.UU.",
    readyToStart = "listo para iniciar",
    trackingInProgress = "millas recorridas (en curso, funciona en segundo plano)",
    startWork = "Start Work",
    stopWork = "Stop Work",
    thisMonth = "Este mes",
    totalMiles = "Millas totales",
    estimatedDeduction = "Deducción estimada",
    platformQuestion = "¿Para qué plataforma trabajaste?",
    customPlatformPlaceholder = "Escribe el nombre de la plataforma",
    tollLabel = "Peajes de este viaje",
    tollHint = "Opcional, ej. 4.50",
    disclaimerHome = "Esta app calcula una ESTIMACIÓN basada en la tasa estándar de millaje del IRS. " +
        "No sustituye asesoría fiscal profesional. Consulta a tu contador para tu declaración.",

    tipTitle = "💡 Tip para no perder millas",
    tipBody = "Activa \"Start Work\" apenas vayas a salir a trabajar — esas millas se pierden si " +
        "empiezas tarde. Y presiona \"Stop Work\" al llegar a casa, para no seguir sumando millas " +
        "que no son de trabajo.",
    tipDontShowAgain = "No mostrar de nuevo hoy",
    tipGotIt = "Entendido",
    tripTooShortError = "No se detectó suficiente distancia recorrida. El viaje no fue guardado.",
    daySplitNotificationTitle = "Nuevo día — viaje guardado automáticamente",
    daySplitNotificationBody = "Cruzaste la medianoche mientras rastreabas. Guardamos las millas de ayer y empezamos un viaje nuevo para hoy, sin que tengas que hacer nada.",
    recoveredTripMessage = "Detectamos un viaje que quedó sin cerrar (se te olvidó presionar \"Stop Work\") y guardamos automáticamente las millas que alcanzamos a rastrear.",
    tollDetectedTitle = "¿Pagaste peaje?",
    tollDetectedBody = "Detectamos que tu ruta pasó cerca de una caseta de peaje conocida. No sabemos el monto exacto (varía según tu vehículo, hora y descuentos) — si pagaste, anótalo aquí para no perder esa deducción.",
    tollDetectedIgnore = "No pagué",

    historyTitle = "Historial de viajes",
    tripsRegistered = "viaje(s) registrados",
    noTripsYet = "Aún no tienes viajes guardados.\nPresiona \"Start Work\" para comenzar a rastrear.",
    deleteTripTitle = "Eliminar viaje",
    deleteTripConfirm = "¿Seguro que quieres eliminar este registro?",
    cancel = "Cancelar",
    delete = "Eliminar",
    noPlatform = "Sin plataforma",
    irsRateLabel = "Tasa IRS",
    edit = "Editar",
    editTripTitle = "Editar viaje",
    addTripTitle = "Agregar viaje manual",
    addTripFab = "Agregar viaje",
    manualTripExplanation = "¿Se te olvidó presionar \"Start Work\"? Agrega el viaje a mano para no perder la deducción.",
    fieldDate = "Fecha (AAAA-MM-DD)",
    fieldMiles = "Millas",
    fieldNote = "Nota (opcional)",
    save = "Guardar",
    invalidMilesError = "Ingresa un número de millas válido, mayor que 0",

    summaryTitle = "Resumen fiscal (IRS)",
    summarySubtitle = "Estimación de deducción por millaje — válida en los 50 estados",
    periodMonth = "Este mes",
    periodQuarter = "Trimestre",
    periodYear = "Este año",
    estimatedDeductionLabel = "Deducción por millaje",
    totalMilesLabel = "Millas totales",
    tripsLabel = "Viajes",
    totalTollsLabel = "Peajes",
    combinedDeductionLabel = "Deducción total estimada",
    tollExplanation = "El IRS permite deducir peajes y estacionamiento de negocio POR SEPARADO, " +
        "además de la deducción estándar por millaje — no están incluidos en la tasa por milla.",
    irsRatesUsed = "Tasas del IRS usadas",
    aboutYourState = "Sobre tu estado",
    chooseYourState = "Elige tu estado",
    summaryDisclaimer = "Este resumen es solo una guía informativa. No constituye asesoría legal ni " +
        "fiscal. Verifica siempre con un profesional certificado (CPA) o con el IRS (irs.gov).",
    donateButton = "Apoya este proyecto — Donar con PayPal",
    platformBreakdownTitle = "Totales por plataforma",

    settingsTitle = "Ajustes",
    settingsLanguage = "Idioma",
    settingsTheme = "Tema",
    settingsState = "Tu estado",
    detectStateButton = "Detectar mi estado por GPS",
    detectStateDetecting = "Detectando tu ubicación…",
    detectStateFoundPrefix = "Detectamos que estás en",
    detectStateError = "No pudimos detectar tu estado. Elígelo manualmente en la lista de abajo.",
    detectStatePermissionDenied = "Necesitas dar permiso de ubicación primero (actívalo desde \"Start Work\" en Inicio, o en los ajustes del sistema).",
    themeLight = "Claro",
    themeDark = "Oscuro",
    themeAuto = "Automático",
    settingsBackupSection = "Respaldo de datos",
    backupExportButton = "Exportar respaldo (guardar archivo)",
    backupImportButton = "Importar respaldo (restaurar archivo)",
    backupExplanation = "Guarda un archivo con todos tus viajes. Si desinstalas la app o cambias de " +
        "celular, usa \"Importar respaldo\" para recuperarlos. Recomendado: guarda el archivo en " +
        "Google Drive o mándatelo por correo.",
    backupExportSuccess = "Respaldo guardado correctamente",
    backupImportSuccess = "Datos restaurados correctamente",
    backupImportError = "No se pudo leer el archivo de respaldo",

    tabHome = "Inicio",
    tabHistory = "Historial",
    tabSummary = "Resumen",
    tabSettings = "Ajustes",
)

private val ENGLISH = AppStrings(
    appTitle = "Mileage Tracker",
    greetingMorning = "Good morning",
    greetingAfternoon = "Good afternoon",
    greetingEvening = "Good evening",
    motivationalQuotes = ENGLISH_QUOTES,

    homeSubtitle = "Track your work miles in any U.S. state",
    readyToStart = "ready to start",
    trackingInProgress = "miles tracked (in progress, works in the background)",
    startWork = "Start Work",
    stopWork = "Stop Work",
    thisMonth = "This month",
    totalMiles = "Total miles",
    estimatedDeduction = "Estimated deduction",
    platformQuestion = "Which platform did you work for?",
    customPlatformPlaceholder = "Type the platform name",
    tollLabel = "Tolls for this trip",
    tollHint = "Optional, e.g. 4.50",
    disclaimerHome = "This app calculates an ESTIMATE based on the IRS standard mileage rate. " +
        "It does not replace professional tax advice. Consult your accountant for your filing.",

    tipTitle = "💡 Tip to avoid losing miles",
    tipBody = "Turn on \"Start Work\" as soon as you're about to leave for work — those miles are " +
        "lost if you start late. And press \"Stop Work\" once you're home, so you don't keep adding " +
        "miles that aren't work-related.",
    tipDontShowAgain = "Don't show again today",
    tipGotIt = "Got it",
    tripTooShortError = "Not enough distance was detected. The trip was not saved.",
    daySplitNotificationTitle = "New day — trip saved automatically",
    daySplitNotificationBody = "You crossed midnight while tracking. We saved yesterday's miles and started a new trip for today — no action needed.",
    recoveredTripMessage = "We found a trip that was never closed (you forgot to press \"Stop Work\") and automatically saved the miles we managed to track.",
    tollDetectedTitle = "Did you pay a toll?",
    tollDetectedBody = "We detected your route passed near a known toll booth. We don't know the exact amount (it varies by vehicle, time, and discounts) — if you paid, enter it here so you don't lose that deduction.",
    tollDetectedIgnore = "I didn't pay",

    historyTitle = "Trip history",
    tripsRegistered = "trip(s) recorded",
    noTripsYet = "You don't have any saved trips yet.\nPress \"Start Work\" to start tracking.",
    deleteTripTitle = "Delete trip",
    deleteTripConfirm = "Are you sure you want to delete this record?",
    cancel = "Cancel",
    delete = "Delete",
    noPlatform = "No platform",
    irsRateLabel = "IRS rate",
    edit = "Edit",
    editTripTitle = "Edit trip",
    addTripTitle = "Add manual trip",
    addTripFab = "Add trip",
    manualTripExplanation = "Forgot to press \"Start Work\"? Add the trip by hand so you don't lose the deduction.",
    fieldDate = "Date (YYYY-MM-DD)",
    fieldMiles = "Miles",
    fieldNote = "Note (optional)",
    save = "Save",
    invalidMilesError = "Enter a valid number of miles, greater than 0",

    summaryTitle = "Tax summary (IRS)",
    summarySubtitle = "Mileage deduction estimate — valid in all 50 states",
    periodMonth = "This month",
    periodQuarter = "Quarter",
    periodYear = "This year",
    estimatedDeductionLabel = "Mileage deduction",
    totalMilesLabel = "Total miles",
    tripsLabel = "Trips",
    totalTollsLabel = "Tolls",
    combinedDeductionLabel = "Total estimated deduction",
    tollExplanation = "The IRS allows deducting business tolls and parking SEPARATELY, in addition " +
        "to the standard mileage deduction — they are not included in the per-mile rate.",
    irsRatesUsed = "IRS rates used",
    aboutYourState = "About your state",
    chooseYourState = "Choose your state",
    summaryDisclaimer = "This summary is for informational purposes only. It is not legal or tax " +
        "advice. Always verify with a certified professional (CPA) or the IRS (irs.gov).",
    donateButton = "Support this project — Donate with PayPal",
    platformBreakdownTitle = "Totals by platform",

    settingsTitle = "Settings",
    settingsLanguage = "Language",
    settingsTheme = "Theme",
    settingsState = "Your state",
    detectStateButton = "Detect my state via GPS",
    detectStateDetecting = "Detecting your location…",
    detectStateFoundPrefix = "We detected you're in",
    detectStateError = "We couldn't detect your state. Choose it manually from the list below.",
    detectStatePermissionDenied = "You need to grant location permission first (turn it on from \"Start Work\" on Home, or in system settings).",
    themeLight = "Light",
    themeDark = "Dark",
    themeAuto = "Automatic",
    settingsBackupSection = "Data backup",
    backupExportButton = "Export backup (save file)",
    backupImportButton = "Import backup (restore file)",
    backupExplanation = "Save a file with all your trips. If you uninstall the app or switch phones, " +
        "use \"Import backup\" to get them back. Recommended: save the file to Google Drive or email " +
        "it to yourself.",
    backupExportSuccess = "Backup saved successfully",
    backupImportSuccess = "Data restored successfully",
    backupImportError = "Couldn't read the backup file",

    tabHome = "Home",
    tabHistory = "History",
    tabSummary = "Summary",
    tabSettings = "Settings",
)

fun stringsFor(language: AppLanguage): AppStrings =
    if (language == AppLanguage.ENGLISH) ENGLISH else SPANISH

/** Permite acceder a los textos desde cualquier @Composable con LocalAppStrings.current */
val LocalAppStrings = compositionLocalOf { SPANISH }
