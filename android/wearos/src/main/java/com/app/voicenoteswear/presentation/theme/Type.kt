package com.app.voicenoteswear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Typography
import com.app.voicenoteswear.R

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val sfProRoundedFontFamilyThin = FontFamily(Font(R.font.sf_pro_rounded_thin))// W100
val sfProRoundedFontFamilyExtraLight = FontFamily(Font(R.font.sf_pro_rounded_ultralight))// W200
val sfProRoundedFontFamilyLight = FontFamily(Font(R.font.sf_pro_rounded_light))// W300
val sfProRoundedFontFamilyNormal = FontFamily(Font(R.font.sf_pro_rounded_regular))// W400
val sfProRoundedFontFamilyMedium = FontFamily(Font(R.font.sf_pro_rounded_medium))// W500
val sfProRoundedFontFamilySemiBold = FontFamily(Font(R.font.sf_pro_rounded_semibold))// W600
val sfProRoundedFontFamilyBold = FontFamily(Font(R.font.sf_pro_rounded_bold))// W700
val sfProRoundedFontFamilyExtraBold = FontFamily(Font(R.font.sf_pro_rounded_heavy))// W800
val sfProRoundedFontFamilyBlack = FontFamily(Font(R.font.sf_pro_rounded_black))// W900

val TextStyle.Companion.Welcome
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyBold,
        color = Color.White,
        letterSpacing = (-0.3).sp,
        fontSize = 14.sp,
        lineHeight = 17.sp,
    )

val TextStyle.Companion.White12
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyBold,
        color = White1,
        fontSize = 12.sp,
        lineHeight = 14.sp,
    )
val TextStyle.Companion.Opacity50Size10
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilySemiBold,
        color = Color.White,
        fontSize = 10.sp,
        lineHeight = 12.sp,
    )
val TextStyle.Companion.Opacity50Size10White1
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyBold,
        color = White1Opacity50,
        fontSize = 10.sp,
        lineHeight = 12.sp,
    )

val TextStyle.Companion.Opacity70Size11
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyMedium,
        color = Color.White,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    )

val TextStyle.Companion.NoteDuration
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilySemiBold,
        color = Transparent50,
        fontSize = 10.sp,
        lineHeight = 12.sp,
    )

val TextStyle.Companion.NoteDate
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilySemiBold,
        color = Transparent20,
        fontSize = 10.sp,
        lineHeight = 12.sp,
    )

val TextStyle.Companion.NoteTitle
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyBold,
        color = White1,
        fontSize = 12.sp,
        lineHeight = 14.sp,
    )

val TextStyle.Companion.NoteTranscript
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyMedium,
        color = Transparent70,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    )

val TextStyle.Companion.NoteSynced
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyMedium,
        color = Blue,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    )

val TextStyle.Companion.DetailTimerBlue
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyBold,
        color = Blue,
        fontSize = 12.sp,
        lineHeight = 14.sp,
    )

val TextStyle.Companion.TranscriptingAudio
    @Composable
    get() = TextStyle(
        fontFamily = sfProRoundedFontFamilyBold,
        color = Green,
        fontSize = 12.sp,
        lineHeight = 14.sp,
    )