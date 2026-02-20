package com.eventurary

import android.app.Application
import com.eventurary.di.logging.NapierKoinLogger
import com.eventurary.di.allModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin

class EventuraryApp: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
        Napier.d { "Logging initialised" }

        startKoin {
            logger(NapierKoinLogger())
            androidContext(this@EventuraryApp)
            modules(allModules)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}

