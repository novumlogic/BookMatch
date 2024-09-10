package com.novumlogic.bookmatch.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val light_orange = Color(0xffFCEEE2)
val md_sys_light_primary = Color(0xFFFA8E1D)
val md_sys_light_primary_95 = Color(0xFFFFF3E7)
val md_sys_light_onPrimary = Color(0xFFFFFFFF)
val md_sys_light_surface = Color(0xFFFEF7FF)
val md_sys_light_outline = Color(0xffD77106)
val md_sys_light_onSurfaceVariant = Color(0xff49454F)
val md_sys_light_secondaryContainer = Color(0xffFFF4E4)
val md_sys_light_primaryContainer = Color(0xffFFE3C7)
val md_sys_light_onPrimaryContainer = Color(0xffBB6000)

val md_sys_light_onSecondaryContainer = Color(0xff342B1E)
val md_sys_light_secondary_fixed_dim = Color(0xffE3D8C8)
val md_sys_light_onSecondaryContainer_8o = Color(0x141D192B)

val bookMatchColors = lightColorScheme(
    primary = md_sys_light_primary,
    onPrimary = md_sys_light_onPrimary,
    primaryContainer = md_sys_light_primaryContainer,
    surface = md_sys_light_surface,
    outline = md_sys_light_outline,
    onSurfaceVariant = md_sys_light_onSurfaceVariant,
    secondaryContainer = md_sys_light_secondaryContainer,
    onSecondaryContainer = md_sys_light_onSecondaryContainer,
    onPrimaryContainer = md_sys_light_onPrimaryContainer
)

@Composable
fun BookMatchTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = bookMatchColors, typography = typography) {
        content()
    }
}