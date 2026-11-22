package ua.chnu.kpp.recipes

import android.app.Application
import co.touchlab.kermit.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import ua.chnu.kpp.recipes.di.initKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AppApplication)
            androidLogger()
        }
        Logger.withTag("Smakolyk").i { "Application started" }
    }
}
