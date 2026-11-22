package ua.chnu.kpp.recipes

import ua.chnu.kpp.recipes.di.initKoin

/** Called from Swift (`KoinInitIosKt.doInitKoinIos()`) before the first Compose screen. */
fun initKoinIos() {
    initKoin()
}
