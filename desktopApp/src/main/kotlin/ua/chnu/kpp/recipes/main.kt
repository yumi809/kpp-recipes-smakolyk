package ua.chnu.kpp.recipes

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ua.chnu.kpp.recipes.di.initKoin

fun main() {
    initKoin { printLogger() }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Smakolyk",
            state = rememberWindowState(width = 420.dp, height = 820.dp),
        ) {
            App()
        }
    }
}
