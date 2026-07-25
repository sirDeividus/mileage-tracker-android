package com.tuusuario.mileagetracker.util

/**
 * UsStates.kt  (NUEVO — generaliza la app a los 50 estados)
 * -----------------------------------------------------------------------
 * La tasa de deducción de millas del IRS (ver TaxRates.kt) es FEDERAL:
 * es exactamente la misma sin importar en qué estado vivas. Lo único que
 * cambiaba de un estado a otro era el texto informativo de la pantalla
 * de Resumen (antes fijo a "North Carolina").
 *
 * Esta lista permite que cualquier usuario, en cualquier estado, elija
 * el suyo y vea la nota correcta.
 *
 * NOTA: noStateIncomeTax refleja los estados que, a la fecha de este
 * código, no cobran impuesto estatal sobre el ingreso personal (Alaska,
 * Florida, Nevada, New Hampshire [solo grava intereses/dividendos, en
 * proceso de eliminarlo], South Dakota, Tennessee, Texas, Washington,
 * Wyoming). Las leyes estatales cambian; si tienes dudas sobre tu caso,
 * confirma siempre con el Departamento de Ingresos (Department of
 * Revenue) de tu estado.
 * -----------------------------------------------------------------------
 */
data class UsState(
    val code: String,
    val displayName: String,
    val noStateIncomeTax: Boolean = false,
)

val US_STATES = listOf(
    UsState("AL", "Alabama"),
    UsState("AK", "Alaska", noStateIncomeTax = true),
    UsState("AZ", "Arizona"),
    UsState("AR", "Arkansas"),
    UsState("CA", "California"),
    UsState("CO", "Colorado"),
    UsState("CT", "Connecticut"),
    UsState("DE", "Delaware"),
    UsState("DC", "Distrito de Columbia (DC)"),
    UsState("FL", "Florida", noStateIncomeTax = true),
    UsState("GA", "Georgia"),
    UsState("HI", "Hawái"),
    UsState("ID", "Idaho"),
    UsState("IL", "Illinois"),
    UsState("IN", "Indiana"),
    UsState("IA", "Iowa"),
    UsState("KS", "Kansas"),
    UsState("KY", "Kentucky"),
    UsState("LA", "Luisiana"),
    UsState("ME", "Maine"),
    UsState("MD", "Maryland"),
    UsState("MA", "Massachusetts"),
    UsState("MI", "Michigan"),
    UsState("MN", "Minnesota"),
    UsState("MS", "Misisipi"),
    UsState("MO", "Misuri"),
    UsState("MT", "Montana"),
    UsState("NE", "Nebraska"),
    UsState("NV", "Nevada", noStateIncomeTax = true),
    UsState("NH", "New Hampshire", noStateIncomeTax = true),
    UsState("NJ", "Nueva Jersey"),
    UsState("NM", "Nuevo México"),
    UsState("NY", "Nueva York"),
    UsState("NC", "Carolina del Norte"),
    UsState("ND", "Dakota del Norte"),
    UsState("OH", "Ohio"),
    UsState("OK", "Oklahoma"),
    UsState("OR", "Oregón"),
    UsState("PA", "Pensilvania"),
    UsState("RI", "Rhode Island"),
    UsState("SC", "Carolina del Sur"),
    UsState("SD", "Dakota del Sur", noStateIncomeTax = true),
    UsState("TN", "Tennessee", noStateIncomeTax = true),
    UsState("TX", "Texas", noStateIncomeTax = true),
    UsState("UT", "Utah"),
    UsState("VT", "Vermont"),
    UsState("VA", "Virginia"),
    UsState("WA", "Washington", noStateIncomeTax = true),
    UsState("WV", "Virginia Occidental"),
    UsState("WI", "Wisconsin"),
    UsState("WY", "Wyoming", noStateIncomeTax = true),
)

fun findStateByCode(code: String): UsState? = US_STATES.find { it.code == code }

/**
 * Genera la nota informativa para el estado elegido. La deducción en sí
 * (millas × tasa del IRS) es idéntica en todos los estados: lo único
 * que cambia es cómo se declara a nivel estatal.
 */
fun stateTaxNotes(state: UsState): String {
    return if (state.noStateIncomeTax) {
        "${state.displayName} no cobra impuesto estatal sobre el ingreso personal, por lo que la " +
            "deducción de millas aplica principalmente a tu declaración FEDERAL ante el IRS. " +
            "Consulta a un profesional de impuestos para confirmar tu caso particular."
    } else {
        "${state.displayName} generalmente usa la tasa estándar de millaje del IRS como base para " +
            "la deducción, igual que a nivel federal. La forma exacta de declararlo varía según tu " +
            "situación (empleado 1099, dueño de negocio, etc.). Consulta siempre a un profesional de " +
            "impuestos o al Departamento de Ingresos (Department of Revenue) de ${state.displayName} " +
            "para tu caso particular."
    }
}
