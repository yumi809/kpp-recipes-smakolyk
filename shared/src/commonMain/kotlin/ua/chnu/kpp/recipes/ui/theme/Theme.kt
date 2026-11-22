package ua.chnu.kpp.recipes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Paprika,
    onPrimary = Color.White,
    primaryContainer = Apricot,
    onPrimaryContainer = PaprikaDark,
    secondary = Basil,
    onSecondary = Color.White,
    secondaryContainer = BasilLight,
    onSecondaryContainer = Cocoa,
    tertiary = Saffron,
    onTertiary = Cocoa,
    background = Cream,
    onBackground = Cocoa,
    surface = Cream,
    onSurface = Cocoa,
    surfaceVariant = CreamDim,
    onSurfaceVariant = CocoaSoft,
    outline = CocoaSoft,
    error = ErrorRed,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = PaprikaLight,
    onPrimary = PaprikaDark,
    primaryContainer = PaprikaDark,
    onPrimaryContainer = Apricot,
    secondary = BasilLight,
    onSecondary = Espresso,
    secondaryContainer = Basil,
    onSecondaryContainer = Latte,
    tertiary = Saffron,
    onTertiary = Espresso,
    background = Espresso,
    onBackground = Latte,
    surface = Espresso,
    onSurface = Latte,
    surfaceVariant = EspressoRaised,
    onSurfaceVariant = Latte.copy(alpha = 0.75f),
    outline = Latte.copy(alpha = 0.5f),
    error = ErrorRedLight,
    onError = Espresso,
)

private val SmakolykShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(28.dp),
)

@Composable
fun SmakolykTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = smakolykTypography(),
        shapes = SmakolykShapes,
        content = content,
    )
}
