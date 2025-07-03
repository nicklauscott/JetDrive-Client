package com.niclauscott.jetdrive.features.file.ui.screen.file_list.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.ui.component.JetDriveSearchField
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.features.file.ui.component.CustomDropDownMenu

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FileScreenTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    isRoot: Boolean = false,
    isSearch: Boolean = true,
    searchText: String,
    onSearchClick: () -> Unit = { },
    onMoreClick: () -> Unit = { },
    onSearchTextChange: (String) -> Unit = { },
    onDone: () -> Unit = { },
    onCancelSearch: () -> Unit = { },
    onBackClick: () -> Unit = { }
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    if (isSearch) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }else{
        keyboardController?.hide()
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .height(8.percentOfScreenHeight()),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        val context = LocalContext.current

        AnimatedContent(
            targetState = isSearch,
            transitionSpec = {
                slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } + fadeOut()
            },
            label = "Content Switch"
        ) { screen ->
            when (screen) {
                true -> {
                    Surface(
                        modifier = modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 6.dp,
                        shape = RectangleShape,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ) {
                        JetDriveSearchField(
                            text = searchText,
                            onClickCancel = onCancelSearch,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                keyboardController?.hide()
                                onDone()
                            })
                        ) { onSearchTextChange(it) }
                    }
                }
                false -> {
                    Surface(
                        modifier = modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 6.dp,
                        shape = RectangleShape,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(0.7f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(0.2f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!isRoot) {
                                        IconButton(onClick = onBackClick) {
                                            Icon(
                                                painter = painterResource(R.drawable.back_icon),
                                                contentDescription = getString(
                                                    context,
                                                    R.string.search_icon
                                                ),
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier.fillMaxHeight().weight(0.8f),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                            }


                            Row(
                                modifier = Modifier.fillMaxHeight()
                                    .weight(0.3f),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = onSearchClick) {
                                    Icon(
                                        painter = painterResource(R.drawable.search_icon),
                                        contentDescription = getString(context, R.string.search_icon),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                IconButton(onClick = onMoreClick) {
                                    Icon(
                                        painter = painterResource(R.drawable.more_vert_icon),
                                        contentDescription = getString(context, R.string.search_icon),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                            }

                        }
                    }
                }
            }
        }
    }

}