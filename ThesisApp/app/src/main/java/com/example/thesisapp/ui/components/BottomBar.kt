package com.example.thesisapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.thesisapp.R


@Composable
fun BottomBar(
    fromHistory: Boolean,
    fromHome: Boolean,
    navigateToHome: () -> Unit,
    navigateToHistory: () -> Unit,
) {
    BottomAppBar(
        containerColor = Color.White
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 40.dp)) {
            IconButton(
                onClick = {
                    navigateToHome()
                },
                modifier = Modifier.weight(0.5f, true).testTag("HomeButton")
            ) {
                Icon(
                    modifier = Modifier.size(42.dp),
                    painter = painterResource(id = R.drawable.home_button),
                    contentDescription = stringResource(R.string.home_button_description),
                    tint = if(fromHome) colorResource( R.color.background_color) else Color.Black
                )
            }
            IconButton(
                onClick = {
                    navigateToHistory()
                },
                modifier = Modifier.weight(0.5f, true).testTag("HistoryButton")
            ) {
                Icon(
                    modifier = Modifier.size(42.dp),
                    painter = painterResource(id = R.drawable.history_button),
                    contentDescription = stringResource(R.string.history_screen_description),
                    tint = if(fromHistory) colorResource( R.color.background_color) else Color.Black
                )
            }
        }
    }
}