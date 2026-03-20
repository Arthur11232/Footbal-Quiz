package com.arthuralexandryan.footballquiz.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.utils.Constants
import com.arthuralexandryan.footballquiz.utils.Prefer
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    protected var n: Int = 0
    protected var category_Score: Int = 0
    protected var category_answer_count: Int = 0
    protected var categoryType: String? = null
    protected var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Установка локализации
        val lang = Prefer.getStringPreference(this, Constants.Localization, "ru") ?: "ru"
        setLocale(lang)
    }

    fun setToolbar(title: String?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.title = title
            setSupportActionBar(toolbar)
        }
    }

    fun setLocale(localeName: String) {
        val myLocale = Locale(localeName)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)
    }
}
