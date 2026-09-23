package com.kidsworkbook.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kidsworkbook.app.ui.navigation.WorkbookNavGraph
import com.kidsworkbook.app.ui.theme.KidsWorkbookTheme
import com.kidsworkbook.app.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as WorkbookApplication
        val factory = ViewModelFactory(app, this)

        setContent {
            KidsWorkbookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WorkbookNavGraph(factory = factory)
                }
            }
        }
    }
}
