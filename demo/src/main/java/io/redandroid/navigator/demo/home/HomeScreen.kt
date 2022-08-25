package io.redandroid.navigator.demo.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kotlin.random.Random

@Composable
fun HomeScreen() = homeScreenDestination {
    Column {
        Button(
            onClick = {
                val randomNumber = Random.nextInt()
                navigateToDetails(randomNumber)
            }
        ) {
            Text(text = "Random")
        }
    }
}
