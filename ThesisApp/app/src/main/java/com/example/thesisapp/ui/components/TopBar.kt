package com.example.thesisapp.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.thesisapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    @StringRes
    title: Int,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(title),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.black)
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
            containerColor = colorResource(R.color.white)
        )
    )
}