package ua.chnu.kpp.recipes.ui.common

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

/** How a screen arranges its content for the current window. */
enum class PaneLayout {
    /** Compact width (phones in portrait): everything in one column. */
    SINGLE,

    /** Medium/expanded width (tablets, foldables, desktop, phones in landscape): two side-by-side panes. */
    TWO,
}

/**
 * Picks the layout from the [WindowSizeClass] of the current window.
 * The break point is the Material "medium" width (600 dp).
 */
@Composable
fun rememberPaneLayout(): PaneLayout {
    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    return if (sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        PaneLayout.TWO
    } else {
        PaneLayout.SINGLE
    }
}
