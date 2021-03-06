package de.jonashive.mobile.jackit.ui.composables

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.jonashive.mobile.jackit.entity.Indexer
import de.jonashive.mobile.jackit.ui.theme.blue_accent
import de.jonashive.mobile.jackit.ui.theme.dark_bg
import de.jonashive.mobile.jackit.viewmodel.WebViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.*
import de.jonashive.mobile.jackit.Variable
import de.jonashive.mobile.jackit.entity.Item
import de.jonashive.mobile.jackit.ui.theme.gray
import de.jonashive.mobile.jackit.ui.theme.light_bg
import de.jonashive.mobile.jackit.viewmodel.LoadingState

val webViewModle = WebViewModel.singelton

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showSystemUi = true)
@Composable
fun BrowseScreen() {
    val keyboardController = LocalSoftwareKeyboardController.current

    val indexer by webViewModle.indexers.observeAsState()
    val searchResult by webViewModle.searchResult.observeAsState()
    val loadingState by webViewModle.loadingState.observeAsState()

    val scrollState = rememberLazyListState()

    var selectedIndexer by remember {
        mutableStateOf("")
    }

    var query by remember {
        mutableStateOf("")
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(dark_bg)
    ) {
        val (searchbar, list, selection, loading) = createRefs()

        Column(modifier = Modifier
            .constrainAs(selection) {

                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(searchbar.top, margin = 8.dp)
            }
            .background(dark_bg)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                indexer?.forEach {
                    Indexer(name = it.title ?: "No NAME", selected = selectedIndexer == it.title) {
                        selectedIndexer = it
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .background(dark_bg)
            .constrainAs(list) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(selection.top)
                height = Dimension.fillToConstraints
            }, state = scrollState
        ) {
            item {
                searchResult?.rss?.item?.forEach {
                    Entry(data = it)
                }
            }
        }

        if (loadingState == LoadingState.LOADING) {
            CircularProgressIndicator(modifier = Modifier.constrainAs(loading) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            })
        }

        Row(modifier = Modifier
            .constrainAs(searchbar) {
                bottom.linkTo(parent.bottom, margin = 60.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (text, button) = createRefs()
                TextField(modifier = Modifier
                    .constrainAs(text) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(button.start)
                        width = Dimension.percent(0.8F)
                    }
                    .fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = light_bg,
                        focusedIndicatorColor = blue_accent, //hide the indicator
                        unfocusedIndicatorColor = blue_accent,
                        textColor = gray
                    ), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (selectedIndexer.isNotBlank()) {
                                webViewModle.search(query, selectedIndexer)
                            }
                        }),
                    value = query,
                    onValueChange = {
                        query = it
                    })
                //Search Button
                Button(onClick = {
                    keyboardController?.hide()
                    if (selectedIndexer.isNotBlank()) {
                        webViewModle.search(query, selectedIndexer)
                    }
                }, modifier = Modifier.constrainAs(button) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                    start.linkTo(text.end)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Check")
                }
            }
        }


    }
}

@Preview(showSystemUi = true)
@Composable
fun Entry(data: Item = Item().apply { title = "This is a Title" }) {
    val context = LocalContext.current
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            Toast
                .makeText(context, "Added -> ${data.title}", Toast.LENGTH_SHORT)
                .show()
            if (data.magnet != null) {
                webViewModle.addTransfer(data.magnet) {
                    if (it.code != 200) {
                        webViewModle.errors.postValue("Transfer Failed (${it.code})")
                    }
                }
            } else if (data.guid != null) {
                webViewModle.getMagnetFor(data.guid!!)
            }
        }) {
        val (title, pub, size, seperator) = createRefs()
        Text(
            text = data.title ?: "",
            fontSize = 24.sp,
            color = gray,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                top.linkTo(parent.top, margin = 2.dp)
                width = Dimension.fillToConstraints
            })
        Text(
            text = "${
                data.pubDate?.subSequence(0, 16).toString()
            } | S/P : ${data.seeder}/${data.peer}",
            fontSize = 16.sp,
            color = gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(pub) {
                start.linkTo(parent.start, margin = 8.dp)
                top.linkTo(title.bottom, margin = 2.dp)
            }
        )
        Text(
            text = "${data.gb} Gb",
            fontSize = 16.sp,
            color = if (data.magnet == null && data.guid == null) light_bg else gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(size) {
                end.linkTo(parent.end, margin = 8.dp)
                top.linkTo(title.bottom, margin = 2.dp)
            }
        )
        Box(modifier = Modifier
            .height(1.dp)
            .background(gray)
            .constrainAs(seperator) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(size.bottom, margin = 2.dp)
                width = Dimension.fillToConstraints
            }
            .padding(bottom = 2.dp)
        )
    }

}

@Composable
fun Indexer(name: String, selected: Boolean, onClick: (name: String) -> (Unit)) {
    Box(modifier = Modifier
        .clip(
            RoundedCornerShape(5.dp)
        )
        .background(if (selected) blue_accent else dark_bg)
        .clickable {
            onClick(name)
        }
        .padding(start = 8.dp, end = 8.dp)) {
        Text(text = name, fontSize = 18.sp)
    }
}