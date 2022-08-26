package io.redandroid.navigator.demo.details

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import io.redandroid.navigator.ScreenBuilder

fun ScreenBuilder.detailsScreenDestination() = detailsScreen {
    DetailsScreen(
        myParam,
    )
}

@Composable
private fun DetailsScreen(
    result: Int,
) {
    Column {
        Text(text = "The result is")
        Text(text = "$result", fontWeight = FontWeight.Bold)
    }
}