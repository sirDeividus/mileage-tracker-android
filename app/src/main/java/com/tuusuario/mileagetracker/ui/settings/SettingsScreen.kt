package com.tuusuario.mileagetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuusuario.mileagetracker.data.local.AppLanguage
import com.tuusuario.mileagetracker.data.local.ThemeMode
import com.tuusuario.mileagetracker.data.local.UserPreferences
import com.tuusuario.mileagetracker.ui.theme.LocalAppColors
import com.tuusuario.mileagetracker.ui.theme.PrimaryGreen
import com.tuusuario.mileagetracker.util.LocalAppStrings
import com.tuusuario.mileagetracker.util.US_STATES
import com.tuusuario.mileagetracker.util.findStateByCode

/**
 * SettingsScreen.kt  (NUEVO)
 * -----------------------------------------------------------------------
 * Pantalla donde el usuario elige:
 *   - Idioma de la app (español / inglés)
 *   - Tema (claro / oscuro / automático)
 *   - Su estado de EE.UU. (para las notas fiscales de la pantalla Resumen)
 * -----------------------------------------------------------------------
 */
@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    currentThemeMode: ThemeMode,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val prefs = remember { UserPreferences(context) }

    var selectedStateCode by remember { mutableStateOf(prefs.stateCode) }
    var showStatePicker by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(strings.settingsTitle, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(20.dp))

            // ---- Idioma ----
            SettingsSectionLabel(strings.settingsLanguage)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OptionChip(
                    label = "Español",
                    selected = currentLanguage == AppLanguage.SPANISH,
                    onClick = { onLanguageChange(AppLanguage.SPANISH) },
                    modifier = Modifier.weight(1f)
                )
                OptionChip(
                    label = "English",
                    selected = currentLanguage == AppLanguage.ENGLISH,
                    onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---- Tema ----
            SettingsSectionLabel(strings.settingsTheme)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OptionChip(strings.themeLight, currentThemeMode == ThemeMode.LIGHT, { onThemeModeChange(ThemeMode.LIGHT) }, Modifier.weight(1f))
                OptionChip(strings.themeDark, currentThemeMode == ThemeMode.DARK, { onThemeModeChange(ThemeMode.DARK) }, Modifier.weight(1f))
                OptionChip(strings.themeAuto, currentThemeMode == ThemeMode.AUTO, { onThemeModeChange(ThemeMode.AUTO) }, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---- Estado ----
            SettingsSectionLabel(strings.settingsState)
            val selectedStateName = findStateByCode(selectedStateCode)?.displayName ?: strings.chooseYourState
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(12.dp))
                    .clickable { showStatePicker = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedStateName, fontSize = 14.sp, color = colors.textPrimary, fontWeight = FontWeight.Medium)
                Text("›", fontSize = 18.sp, color = colors.textMuted)
            }
        }
    }

    if (showStatePicker) {
        StatePickerDialog(
            currentCode = selectedStateCode,
            onSelect = { code ->
                prefs.stateCode = code
                selectedStateCode = code
                showStatePicker = false
            },
            onDismiss = { showStatePicker = false }
        )
    }
}

@Composable
private fun SettingsSectionLabel(text: String) {
    val colors = LocalAppColors.current
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textSecondary)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun OptionChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val bg = if (selected) PrimaryGreen else colors.surface
    val textColor = if (selected) androidx.compose.ui.graphics.Color.White else colors.textPrimary

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatePickerDialog(
    currentCode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.chooseYourState, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.height(400.dp)) {
                items(US_STATES, key = { it.code }) { state ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(state.code) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(state.displayName, fontSize = 14.sp)
                        if (state.code == currentCode) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
