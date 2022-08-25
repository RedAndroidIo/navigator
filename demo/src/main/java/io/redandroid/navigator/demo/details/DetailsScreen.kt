package io.redandroid.navigator.demo.details

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DetailsScreen() = detailsScreenDestination {
    Column {
        Text(text = "The result is")
        Text(text = myParam.toString(), fontWeight = FontWeight.Bold)
    }
}