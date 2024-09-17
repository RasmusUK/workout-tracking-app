package com.example.workout_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
                val viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(
                LocalContext.current))
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: WorkoutViewModel)
{
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onWorkoutClick = { workoutId ->
                navController.navigate("workoutDetails/${workoutId}")
            }, viewModel)
        }
        composable(
            "workoutDetails/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId")
            val workout = viewModel.getWorkoutFromId(workoutId)
            if (workout != null) {
                WorkoutScreen(workout, { viewModel.deleteWorkout(workout) } ,navController, { viewModel.saveWorkouts() })
            }
        }
    }
}