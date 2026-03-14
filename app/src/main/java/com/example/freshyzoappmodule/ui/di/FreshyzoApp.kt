package com.example.freshyzoappmodule.ui.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class FreshyzoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FreshyzoApp)
            modules(appModule)
        }
    }
}