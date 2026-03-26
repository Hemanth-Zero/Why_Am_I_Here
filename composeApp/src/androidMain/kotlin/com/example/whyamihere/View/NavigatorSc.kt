package com.example.whyamihere.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.week2.TaskScreen
import com.example.whyamihere.ViewModel.MyAppViewModel
import com.example.whyamiherelab.HomeScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavi(myAppViewModel: MyAppViewModel) {
    val usageList = myAppViewModel.getUsageList()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.HomeScreen.name) {
        val Sc1 = {
            navController.navigate(Screens.TasksScreen.name) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true; restoreState = true
            }
        }
        val Sc2 = {
            navController.navigate(Screens.HomeScreen.name) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true; restoreState = true
            }
        }
        val Sc3 = {
            navController.navigate(Screens.ProfileScreen.name) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true; restoreState = true
            }
        }
        composable(Screens.TasksScreen.name) {
            TaskScreen(Sc3 = Sc3, Sc1 = Sc1, Sc2 = Sc2)
        }
        composable(Screens.HomeScreen.name) {
            HomeScreen(usageList, Sc3 = Sc3, Sc1 = Sc1, Sc2 = Sc2)
        }
        composable(Screens.ProfileScreen.name) {
            ProfileScreen(Sc3 = Sc3, Sc1 = Sc1, Sc2 = Sc2, myAppViewModel = myAppViewModel)
        }
    }
}

enum class Screens(val route: String, val id: Int = 3) {
    HomeScreen(route = "HomeSc", id = 2),
    ProfileScreen(route = "ProfileSc", id = 3),
    TasksScreen(route = "TaskSc", id = 1)
}
