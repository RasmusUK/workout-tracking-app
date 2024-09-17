package com.example.workout_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Workout(
    var name: String,
    var id: UUID = UUID.randomUUID(),
    val exercises: MutableList<Exercise> = mutableStateListOf(),
    val date: Date = Date(),
    val dateString: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
)

data class Exercise(
    val name: String,
    val sets: MutableList<Set> = mutableStateListOf()
    )

data class Set(
    val reps: Int,
    val weight: Double,
    )

@Composable
fun ExerciseCard(exercise: Exercise, onSetDelete: (Int) -> Unit, onExerciseDelete: () -> Unit) {
    var newReps by remember { mutableStateOf("") }
    var newWeight by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showExerciseDeleteDialog by remember { mutableStateOf(false) } // Add state for exercise delete dialog
    var showSetDeleteDialog by remember { mutableStateOf(false) } // Add state for set delete dialog
    var setToDelete by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { showExerciseDeleteDialog = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete exercise")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            exercise.sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${set.reps} reps x ${set.weight} kg")
                    IconButton(onClick = {
                        setToDelete = index
                        showSetDeleteDialog = true
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete set")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showDialog = true }) {
                Text("Add set")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Add set") },
                    text = {
                        Column {
                            TextField(
                                value = newReps,
                                onValueChange = { newReps = it },
                                label = { Text("Reps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            TextField(
                                value = newWeight,
                                onValueChange = { newWeight = it },
                                label = { Text("Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val reps = newReps.toIntOrNull() ?: 0
                            val weight = newWeight.toDoubleOrNull() ?: 0.0
                            exercise.sets.add(Set(reps, weight))
                            newReps = ""
                            newWeight = ""
                            showDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
    ConfirmationDialog(
        showDialog = showExerciseDeleteDialog,
        onDismiss = { showExerciseDeleteDialog = false },
        onConfirm = onExerciseDelete,
        title = "Confirm Delete",
        text = "Are you sure you want to delete this exercise?"
    )

    ConfirmationDialog(
        showDialog = showSetDeleteDialog,
        onDismiss = {
            showSetDeleteDialog = false
            setToDelete = null
        },
        onConfirm = {
            setToDelete?.let {
                onSetDelete(it)
                showSetDeleteDialog = false
                setToDelete = null
            }
        },
        title = "Confirm Delete",
        text = "Are you sure you want to delete this set?"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(workout: Workout, onWorkoutDelete: () -> Unit, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var newExerciseName by remember { mutableStateOf("") }
    var showWorkoutDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Workout details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "backIcon")
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add exercise")
            }
        }
    ) { contentPadding ->
        LazyColumn(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Name: ${workout.name}", style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Date: ${workout.dateString}", style = MaterialTheme.typography.titleSmall)
                        IconButton(onClick = {
                            showWorkoutDeleteDialog = true
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Workout")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            items(workout.exercises) { exercise ->
                ExerciseCard(exercise, onSetDelete = { index ->
                    exercise.sets.removeAt(index)
                },
                onExerciseDelete = {
                    workout.exercises.remove(exercise)
                })
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add exercise") },
                text = {
                    TextField(
                        value = newExerciseName,
                        onValueChange = { newExerciseName = it },
                        label = { Text("Name") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (newExerciseName.isNotBlank()) {
                            workout.exercises.add(Exercise(name = newExerciseName))
                            newExerciseName = ""
                            showDialog = false
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
    ConfirmationDialog(
        showDialog = showWorkoutDeleteDialog,
        onDismiss = { showWorkoutDeleteDialog = false },
        onConfirm = {
            onWorkoutDelete()
            navController.popBackStack()
        },
        title = "Confirm Delete",
        text = "Are you sure you want to delete this workout?"
    )
}