package de.jonashive.mobile.jackit.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.jonashive.mobile.jackit.Variable
import de.jonashive.mobile.jackit.VariablesViewModel
import de.jonashive.mobile.jackit.ui.theme.blue_accent
import de.jonashive.mobile.jackit.ui.theme.gray
import de.jonashive.mobile.jackit.ui.theme.light_bg

@Preview
@Composable
fun MoreScreen(){
    val vmod = VariablesViewModel.singelton

    val textColors = TextFieldDefaults.textFieldColors(
        backgroundColor = light_bg,
        focusedIndicatorColor =  blue_accent, //hide the indicator
        unfocusedIndicatorColor = Color.Transparent)

    var url by remember {
        mutableStateOf(vmod.read(Variable.BASE_URL))
    }
    var port by remember {
        mutableStateOf(vmod.read(Variable.PORT))
    }

    var apikey by remember {
        mutableStateOf(vmod.read(Variable.JACKETT_API_KEY))
    }


    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.padding(top = 8.dp, start = 8.dp), fontSize = 45.sp, text = "Settings", color = gray)
            Text(modifier = Modifier.padding(start = 25.dp, top = 8.dp), fontSize = 18.sp, maxLines = 1, text = "Jackett Base Url:", color = gray)
            TextField(modifier = Modifier.padding(start = 35.dp, end = 25.dp, top = 8.dp).fillMaxWidth(), singleLine = true, colors = textColors
                , value = url, onValueChange = {
                    vmod.write(Variable.BASE_URL, it)
                    url = it
                })
            Text(modifier = Modifier.padding(start = 25.dp, top = 8.dp), maxLines = 1, text = "Port:", color = gray)
            TextField(modifier = Modifier.padding(start = 35.dp, end = 25.dp, top = 8.dp).fillMaxWidth(), singleLine = true, colors = textColors
                , value = port, onValueChange = {
                    vmod.write(Variable.PORT, it)
                    port = it
                })
            Text(modifier = Modifier.padding(start = 25.dp, top = 8.dp), maxLines = 1, text = "API Key:", color = gray)
            TextField(modifier = Modifier.padding(start = 35.dp, end = 25.dp, top = 8.dp).fillMaxWidth(), singleLine = true, colors = textColors
                , value = apikey, onValueChange = {
                    vmod.write(Variable.JACKETT_API_KEY, it)
                    apikey = it
                })
        }
    }
}