package com.example.workout_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.example.workout_app.ui.theme.WorkoutappTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkoutappTheme {
                val workouts = remember {
                    mutableStateListOf<Workout>()
                }
                AppNavigation(workouts)
            }
        }
    }
}

@Composable
fun AppNavigation(workouts: MutableList<Workout>) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(workouts) { workoutId ->
                navController.navigate("workoutDetails/${workoutId}")
            }
        }
        composable(
            "workoutDetails/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId")
            val workout = workouts.find { it.id.toString() == workoutId }
            if (workout != null) {
                WorkoutScreen(workout, { workouts.remove(workout) } ,navController)
            }
        }
    }
}