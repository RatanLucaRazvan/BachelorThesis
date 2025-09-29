package com.example.thesisapp.ui.screens

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesisapp.R
import com.example.thesisapp.data.news.News
import com.example.thesisapp.ui.navigation.NavigationDestination
import com.example.thesisapp.ui.viewmodel.ApplicationViewModel
import com.example.thesisapp.ui.viewmodel.DetectionUiState
import com.example.thesisapp.utils.countWords
import java.time.LocalDate

const val MAX_WORD_COUNT = 550

object HomeDestination : NavigationDestination {
    override val route: String
        get() = "home"
    override val titleRes: Int
        get() = R.string.home_title

}

@Composable
fun HomeScreen(
    viewModel: ApplicationViewModel,
    modifier: Modifier = Modifier
) {
    HomeContent(
        modifier = modifier
            .fillMaxSize()
            .testTag("HomeScreenContent"),
        viewModel = viewModel
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: ApplicationViewModel
) {
    var content by remember { mutableStateOf("") }
    var showDetectionDialog by remember { mutableStateOf(false) }
    var wordCount by remember { mutableIntStateOf(0) }
    var isContentError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        modifier = modifier
            .background(color = colorResource(R.color.background_color))
            .padding(top = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.home_screen_title),
            modifier = Modifier.padding(horizontal = 10.dp),
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        OutlinedTextField(
            value = content,
            onValueChange = { newValue ->
                content = newValue
                wordCount = countWords(newValue)
                isContentError = wordCount > MAX_WORD_COUNT
            },
            label = { Text(text = stringResource(R.string.news_field_label)) },
            modifier = Modifier
                .size(320.dp)
                .padding(16.dp),
            colors = OutlinedTextFieldDefaults.colors().copy(
                unfocusedContainerColor = colorResource(R.color.white),
                focusedContainerColor = colorResource(R.color.white),
                unfocusedLabelColor = colorResource(R.color.black),
                focusedLabelColor = colorResource(R.color.black),
                focusedTextColor = colorResource(R.color.black),
                unfocusedTextColor = colorResource(R.color.black),
                errorTextColor = colorResource(R.color.black),
                focusedIndicatorColor = colorResource(R.color.black),
                unfocusedIndicatorColor = colorResource(R.color.black),
                cursorColor = colorResource(R.color.black),
                errorContainerColor = colorResource(R.color.white),
                errorIndicatorColor = Color.Red,
                errorLabelColor = Color.Red,
            ),
            isError = isContentError,
            supportingText = {
                Column {
                    Text(
                        text = "$wordCount/$MAX_WORD_COUNT words",
                        color = if (isContentError) Color.Red else colorResource(R.color.black),
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
        )
        Button(
            onClick = {
                viewModel.checkNews(content)
                showDetectionDialog = true
            },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = colorResource(R.color.white),
                contentColor = colorResource(R.color.black),
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.Gray,
            ),
            enabled = content.isNotEmpty() && !isContentError,
        ) {
            Text(text = stringResource(R.string.check_news_button))
        }

        val detectionUiState = viewModel.detectionUiState
        if (showDetectionDialog) {
            BasicAlertDialog(
                onDismissRequest = { showDetectionDialog = false },
                modifier = Modifier
                    .size(350.dp, 250.dp)
                    .background(
                        color = colorResource(R.color.white),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                when (detectionUiState) {
                    is DetectionUiState.Loading -> {
                        Image(
                            modifier = Modifier.size(50.dp),
                            painter = painterResource(R.drawable.loading_circle),
                            contentDescription = "Loading",
                        )
                    }

                    is DetectionUiState.Success -> {
                        Column(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .weight(0.5f, true)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(color = colorResource(R.color.black))) {
                                            append(stringResource(R.string.detection_dialog_title))
                                        }
                                        append(" ")
                                        if (detectionUiState.detectionIsFake) {
                                            withStyle(style = SpanStyle(color = Color.Red)) {
                                                append(stringResource(R.string.fake_label).uppercase())
                                            }
                                        } else {
                                            withStyle(style = SpanStyle(color = Color.Green)) {
                                                append(stringResource(R.string.real_label).uppercase())
                                            }
                                        }
                                    },
                                    fontSize = 18.sp,
                                )
                                Text(
                                    text = stringResource(R.string.detection_dialog_message),
                                    color = colorResource(R.color.black),
                                    modifier = Modifier.padding(start = 18.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .weight(0.5f, true)
                                    .fillMaxWidth()
                            ) {
                                Spacer(modifier = Modifier.weight(0.8f, true))
                                Button(
                                    onClick = {
                                        showDetectionDialog = false
                                        content = ""
                                    }, colors = ButtonColors(
                                        containerColor = colorResource(R.color.white),
                                        contentColor = colorResource(R.color.black),
                                        disabledContainerColor = Color.Gray,
                                        disabledContentColor = Color.Gray,
                                    )
                                ) {
                                    Text(text = stringResource(R.string.cancel_button))
                                }
                                Button(
                                    onClick = {
                                            try {
                                                val currentNews = News(
                                                    content = content,
                                                    checkDate = LocalDate.now().toString(),
                                                    isFake = detectionUiState.detectionIsFake
                                                )

                                                viewModel.addNews(currentNews)
                                                showDetectionDialog = false
                                                content = ""
                                            } catch (e: SQLiteConstraintException) {
                                                Log.e(
                                                    "CreateScreenContent",
                                                    "Movie with duplicate id"
                                                )
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.duplicate_movie_error),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } catch (e: SQLiteException) {
                                                Log.e("CreateScreenContent", "Error on add")
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.persistence_error),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }, colors = ButtonColors(
                                        containerColor = colorResource(R.color.white),
                                        contentColor = colorResource(R.color.black),
                                        disabledContainerColor = Color.Gray,
                                        disabledContentColor = Color.Gray,
                                    )
                                ) {
                                    Text(text = stringResource(R.string.save_news_button))
                                }
                            }
                        }
                    }

                    is DetectionUiState.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .weight(1f, true)
                        ) {
                            Text(
                                text = stringResource(R.string.error_fetch_result),
                                color = colorResource(R.color.black)
                            )
                        }
                    }
                }
            }
        }
    }
}