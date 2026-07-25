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

    // Ajustes
    val settingsTitle: String,
    val settingsLanguage: String,
    val settingsTheme: String,
    val settingsState: String,
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

    historyTitle = "Historial de viajes",
    tripsRegistered = "viaje(s) registrados",
    noTripsYet = "Aún no tienes viajes guardados.\nPresiona \"Start Work\" para comenzar a rastrear.",
    deleteTripTitle = "Eliminar viaje",
    deleteTripConfirm = "¿Seguro que quieres eliminar este registro?",
    cancel = "Cancelar",
    delete = "Eliminar",
    noPlatform = "Sin plataforma",
    irsRateLabel = "Tasa IRS",

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

    settingsTitle = "Ajustes",
    settingsLanguage = "Idioma",
    settingsTheme = "Tema",
    settingsState = "Tu estado",
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

    historyTitle = "Trip history",
    tripsRegistered = "trip(s) recorded",
    noTripsYet = "You don't have any saved trips yet.\nPress \"Start Work\" to start tracking.",
    deleteTripTitle = "Delete trip",
    deleteTripConfirm = "Are you sure you want to delete this record?",
    cancel = "Cancel",
    delete = "Delete",
    noPlatform = "No platform",
    irsRateLabel = "IRS rate",

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

    settingsTitle = "Settings",
    settingsLanguage = "Language",
    settingsTheme = "Theme",
    settingsState = "Your state",
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
