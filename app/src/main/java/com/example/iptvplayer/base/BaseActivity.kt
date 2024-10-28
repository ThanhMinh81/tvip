package com.example.matheasyapp.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigator
import androidx.viewbinding.ViewBinding

import java.util.Locale
import java.util.prefs.Preferences
import javax.inject.Inject

abstract class BaseActivity<VB : ViewBinding>(val bindingInflater: (LayoutInflater) -> VB) :
    AppCompatActivity() {

    @Inject
    lateinit var prefs: Preferences

    val binding: VB by lazy { bindingInflater(layoutInflater) }

    abstract fun viewCreated()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        prefs
//            .language
//            .asObservable()
//            .autoDispose(scope())
//            .subscribe { language ->
//                initLanguage(language)
//            }

        setContentView(binding.root)
        viewCreated()
    }

    private fun initLanguage(language: String) {
        val locale = Locale(language)
        val config = resources.configuration.apply {
            setLocale(locale)
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

