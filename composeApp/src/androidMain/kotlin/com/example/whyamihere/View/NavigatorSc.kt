package com.example.whyamihere.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.week2.TaskScreen
import com.example.whyamihere.Model.UsageStatsRepository
import com.example.whyamiherelab.HomeScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavi(usageRepository : UsageStatsRepository){
    val usageList = usageRepository.getTodayUsage()
    val navcontroller = rememberNavController()
    NavHost(
        navController = navcontroller,
        startDestination = Screens.HomeScreen.name,
    ){
        val Sc1 = {
            navcontroller.navigate(Screens.TasksScreen.name) {
                popUpTo(navcontroller.graph.startDestinationId)
                launchSingleTop = true
                restoreState = true
            }
        }

        val Sc2 = {
            navcontroller.navigate(Screens.HomeScreen.name) {
                popUpTo(navcontroller.graph.startDestinationId)
                launchSingleTop = true
                restoreState = true
            }
        }

        val Sc3 = {
            navcontroller.navigate(Screens.ProfileScreen.name) {
                popUpTo(navcontroller.graph.startDestinationId)
                launchSingleTop = true
                restoreState = true
            }
        }
        composable(route = Screens.TasksScreen.name) {
            TaskScreen(Sc3 = Sc3 , Sc1 = Sc1 , Sc2 = Sc2)
        }
        composable(route = Screens.HomeScreen.name) {
            HomeScreen(usageList , Sc3 = Sc3 , Sc1 = Sc1 , Sc2 = Sc2)
        }
        composable(route = Screens.ProfileScreen.name) {
            ProfileScreen(Sc3 = Sc3 , Sc1 = Sc1 , Sc2 = Sc2 , Sc4 = {})
        }

    }

}

enum class Screens (val route : String , val id: Int = 3){
    HomeScreen(route = "HomeSc", id= 2),
    ProfileScreen(route = " ProfileSc" , id =3),
    TasksScreen(route = "TaskSc" , id = 1)
}