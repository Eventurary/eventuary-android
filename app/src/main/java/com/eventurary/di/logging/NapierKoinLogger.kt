package com.eventurary.di.logging

import io.github.aakira.napier.Napier
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class NapierKoinLogger(level: Level = Level.INFO) : Logger(level) {

    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.INFO -> Napier.i(msg)
            Level.WARNING -> Napier.w(msg)
            Level.DEBUG -> Napier.d(msg)
            Level.ERROR -> Napier.e(msg)
            Level.NONE -> {}
        }
    }
}