package com.example.freshyzoappmodule.ui.di

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class FreshyzoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )

        startKoin {
            androidContext(this@FreshyzoApp)
            modules(appModule)
        }
    }
}