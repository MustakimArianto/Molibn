package com.mustakimarianto.molibndemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mustakimarianto.molibn.compose.DebugPanelHost
import com.mustakimarianto.molibn.compose.utils.PanelButtonPosition
import com.mustakimarianto.molibndemo.ui.theme.MolibnTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val molibn = (application as App).molibn!!

        enableEdgeToEdge()
        setContent {
            MolibnTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DebugPanelHost(molibn, buttonPosition = PanelButtonPosition.BOTTOM_RIGHT, true) {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MolibnTheme {
        Greeting("Android")
    }
}