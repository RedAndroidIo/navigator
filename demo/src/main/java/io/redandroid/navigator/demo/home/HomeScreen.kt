package io.redandroid.navigator.demo.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import io.redandroid.navigator.HomeContext
import io.redandroid.navigator.ScreenBuilder
import kotlin.random.Random

fun ScreenBuilder.homeScreenDestination() = homeScreen {
    HomeScreen()
}

@Composable
private fun HomeContext.HomeScreen() {
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
