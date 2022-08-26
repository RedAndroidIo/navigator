package io.redandroid.navigator.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.redandroid.navigator.Navigator
import io.redandroid.navigator.demo.details.detailsScreenDestination
import io.redandroid.navigator.demo.home.homeScreenDestination
import io.redandroid.navigator.demo.ui.theme.NavGraphConfigComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavGraphConfigComposeTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    Main()
                }
            }
        }
    }
}

@Composable
fun Main() = Navigator {
    homeScreenDestination()
    detailsScreenDestination()
}