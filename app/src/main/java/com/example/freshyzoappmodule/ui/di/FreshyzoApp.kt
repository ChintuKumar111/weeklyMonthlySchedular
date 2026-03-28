package com.example.freshyzoappmodule.ui.di

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class FreshyzoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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