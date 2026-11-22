package ua.chnu.kpp.recipes

import android.app.Application
import co.touchlab.kermit.Logger

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.withTag("Smakolyk").i { "Application started" }
    }
}
