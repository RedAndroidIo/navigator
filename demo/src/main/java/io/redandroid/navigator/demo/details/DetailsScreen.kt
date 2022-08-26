package io.redandroid.navigator.demo.details

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import io.redandroid.navigator.DetailsContext
import io.redandroid.navigator.ScreenBuilder

fun ScreenBuilder.detailsScreenDestination() = detailsScreen {
    DetailsScreen()
}

@Composable
private fun DetailsContext.DetailsScreen() {
    Column {
        Text(text = "The result is")
        Text(text = myParam.toString(), fontWeight = FontWeight.Bold)
    }
}