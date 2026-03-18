package com.example.freshyzoappmodule.helper

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.freshyzoappmodule.data.manager.PreferenceManager
import java.util.Locale

object LanguageManager {
    
    fun setLocale(context: Context, languageCode: String) {
        PreferenceManager.saveLanguage(context, languageCode)
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun getSavedLanguage(context: Context): String {
        return PreferenceManager.getLanguage(context)
    }

    fun onAttach(context: Context): Context {
        val lang = getSavedLanguage(context)
        return updateResources(context, lang)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }
}
