package com.kidsworkbook.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val WorkbookColorScheme = lightColorScheme(
    primary = WorkbookOrange,
    secondary = WorkbookBlue,
    tertiary = WorkbookPurple,
    background = WorkbookBackground,
    surface = WorkbookSurface,
    onBackground = WorkbookTextDark,
    onSurface = WorkbookTextDark
)

private val WorkbookTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)
)

@Composable
fun KidsWorkbookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WorkbookColorScheme,
        typography = WorkbookTypography,
        content = content
    )
}
