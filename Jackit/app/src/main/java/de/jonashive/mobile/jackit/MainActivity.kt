package de.jonashive.mobile.jackit

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import de.jonashive.mobile.jackit.ui.composables.BrowseScreen
import de.jonashive.mobile.jackit.ui.composables.MoreScreen
import de.jonashive.mobile.jackit.ui.theme.JackitTheme
import de.jonashive.mobile.jackit.ui.theme.dark_bg
import de.jonashive.mobile.jackit.ui.theme.light_bg
import de.jonashive.mobile.jackit.viewmodel.WebViewModel

class MainActivity : ComponentActivity() {
    val webViewModel = WebViewModel.singelton

    override fun onCreate(savedInstanceState: Bundle?) {
        VariablesViewModel(this)
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = dark_bg,
                    darkIcons = false
                )

            }
            MainScreen()
        }


        webViewModel.errors.observe(this){
            if (!it.isNullOrBlank()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun MainScreen(){
    JackitTheme(true) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val navController = rememberNavController()

            val bottomNavigationItems = listOf(
                BottomNavigationScreens.library,
                BottomNavigationScreens.updates,
                BottomNavigationScreens.history,
                BottomNavigationScreens.browse,
                BottomNavigationScreens.more
            )

            Scaffold(
                bottomBar = {
                    MainBottomNavigation(navController, bottomNavigationItems)
                },
            ) {
                MainScreenNavigationConfigurations(navController)
            }
        }
    }

}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController
) {
    NavHost(navController, startDestination = BottomNavigationScreens.library.route) {
        composable(BottomNavigationScreens.library.route) {
            Greeting(name = "Lib")
        }
        composable(BottomNavigationScreens.updates.route) {
            Greeting(name = "UPDATES")
        }
        composable(BottomNavigationScreens.history.route) {
            Greeting(name = "HISTORY")
        }
        composable(BottomNavigationScreens.browse.route) {
            BrowseScreen()
            val webViewModle = WebViewModel.singelton
            webViewModle.getIndexer()
        }
        composable(BottomNavigationScreens.more.route) {
            MoreScreen()
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JackitTheme {
        Greeting("Android")
    }
}