package de.jonashive.mobile.jackit

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import de.jonashive.mobile.jackit.ui.theme.blue_accent
import de.jonashive.mobile.jackit.ui.theme.gray

@SuppressLint("ResourceType")
@Composable
fun MainBottomNavigation(
    navController: NavHostController,
    items: List<BottomNavigationScreens>
) {
    BottomNavigation {
        var currentRoute by remember {
            mutableStateOf("Menu")
        }
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {Icon(imageVector = screen.icon , contentDescription = "")},
                selectedContentColor = blue_accent,
                unselectedContentColor = gray,
                label = { Text(stringResource(id = screen.resourceId)) },
                selected = currentRoute == screen.route,
                onClick = {
                    // This if check gives us a "singleTop" behavior where we do not create a
                    // second instance of the composable if we are already on that destination
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                        currentRoute = screen.route
                    }
                }

            )
        }
    }
}

sealed class BottomNavigationScreens(val route: String, @StringRes val resourceId: Int, val icon: ImageVector ) {
    object library: BottomNavigationScreens("menu", R.string.menu, Icons.Rounded.Home)
    object updates : BottomNavigationScreens("updates", R.string.updates, Icons.Rounded.Info)
    object history : BottomNavigationScreens("history", R.string.history, Icons.Rounded.Refresh)
    object browse : BottomNavigationScreens("browse", R.string.browse, Icons.Rounded.Search)
    object more : BottomNavigationScreens("more", R.string.more, Icons.Rounded.MoreVert)
}