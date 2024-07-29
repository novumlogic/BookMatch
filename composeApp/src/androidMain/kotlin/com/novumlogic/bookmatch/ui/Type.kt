package com.novumlogic.bookmatch.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.novumlogic.bookmatch.R

val roboto_regular = Font(
    resId = R.font.roboto_regular,
)
val roboto_medium = Font(R.font.roboto_medium)
val fontFamilyRoboto = FontFamily(listOf(roboto_regular, roboto_medium))
val typography = Typography(
    displayMedium = TextStyle(
        fontFamily = fontFamilyRoboto,
        lineHeight = 52.sp,
        fontSize = 45.sp,
    ),
    titleMedium =  TextStyle(
        fontFamily = fontFamilyRoboto,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (0.15).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = fontFamilyRoboto,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    labelLarge = TextStyle(
        fontFamily = fontFamilyRoboto,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyMedium = TextStyle(
        fontFamily = fontFamilyRoboto,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (0.25).sp
    ),
    bodySmall = TextStyle(
        fontFamily = fontFamilyRoboto,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )
)