package com.example.hcmus_quickhelper.core.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageUtils {
    const val LANG_EN = "en"
    const val LANG_VI = "vi"

    fun wrapContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}
