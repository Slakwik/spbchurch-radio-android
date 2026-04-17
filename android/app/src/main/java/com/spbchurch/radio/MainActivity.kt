package com.spbchurch.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.spbchurch.radio.ui.SPBChurchRadioApp
import com.spbchurch.radio.ui.theme.SPBChurchRadioTheme
import com.spbchurch.radio.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel = MainViewModel(application)
        
        enableEdgeToEdge()
        setContent {
            SPBChurchRadioTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SPBChurchRadioApp(viewModel = viewModel)
                }
            }
        }
    }
}
