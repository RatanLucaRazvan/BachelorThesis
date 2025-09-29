package com.example.thesisapp.ui.components

import android.database.sqlite.SQLiteException
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesisapp.R
import com.example.thesisapp.data.news.News
import com.example.thesisapp.ui.viewmodel.ApplicationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsItem(
    news: News,
    modifier: Modifier = Modifier,
    viewModel: ApplicationViewModel,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Card(shape = RoundedCornerShape(8.dp), modifier = modifier
        .height(120.dp)
        .clickable {
            showBottomSheet = true
        }) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.white))
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = news.content,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    maxLines = 2,
                )
                Text(
                    text = news.checkDate,
                    color = colorResource(R.color.black)
                )
            }

            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    showDeleteDialog = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.delete_button),
                        contentDescription = stringResource(R.string.delete_news_label),
                        tint = colorResource(R.color.black)
                    )
                }
                Text(
                    text = if (news.isFake) stringResource(R.string.fake_label).uppercase() else stringResource(
                        R.string.real_label
                    ).uppercase(),
                    color = if (news.isFake) Color.Red else Color.Green,
                )
            }
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxHeight(),
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    BottomSheetDefaults.DragHandle(modifier = Modifier.align(Alignment.Center))
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 20.dp),
                        onClick = {
                            clipboardManager.setText(
                                AnnotatedString(
                                    news.content
                                )
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.copy_button),
                            contentDescription = stringResource(R.string.copy_button_description),
                            tint = colorResource(R.color.black)
                        )
                    }
                }
            },
            windowInsets = WindowInsets(0)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                Text(
                    text = news.content
                )
            }
        }
    }

    if (showDeleteDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            modifier = Modifier
                .size(300.dp, 200.dp)
                .background(color = colorResource(R.color.white), shape = RoundedCornerShape(20.dp))
        ) {
            Column {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .weight(0.5f, true)
                        .padding(start = 16.dp, top = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.delete_dialog_title),
                        fontSize = 20.sp,
                        color = colorResource(R.color.black)
                    )
                    Text(
                        text = stringResource(R.string.delete_dialog_subtitle),
                        color = colorResource(R.color.black)
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
                        onClick = { showDeleteDialog = false }, colors = ButtonColors(
                            containerColor = colorResource(R.color.white),
                            contentColor = colorResource(R.color.black),
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Gray,
                        )
                    ) {
                        Text(text = stringResource(R.string.dismiss_dialog_button))
                    }
                    Button(
                        onClick = {
                                try {
                                    viewModel.deleteNews(news.id)
                                    showDeleteDialog = false
                                } catch (e: SQLiteException) {
                                    Log.e("HomeScreenContent", "Database error on delete")
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
                        Text(text = stringResource(R.string.confirm_dialog_button))
                    }
                }
            }
        }
    }
}