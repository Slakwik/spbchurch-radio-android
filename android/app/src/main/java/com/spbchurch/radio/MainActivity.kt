package com.spbchurch.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.spbchurch.radio.ui.SPBChurchRadioApp
import com.spbchurch.radio.ui.theme.LocalThemeManager
import com.spbchurch.radio.ui.theme.SPBChurchRadioTheme
import com.spbchurch.radio.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel(application)
        val themeManager = (application as RadioApplication).themeManager

        enableEdgeToEdge()
        setContent {
            val mode by themeManager.mode.collectAsState()
            CompositionLocalProvider(LocalThemeManager provides themeManager) {
                SPBChurchRadioTheme(themeMode = mode) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SPBChurchRadioApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
