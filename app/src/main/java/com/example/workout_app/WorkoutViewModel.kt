package com.example.workout_app

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class WorkoutViewModel(private val storage: WorkoutStorage) : ViewModel() {
    private val _workouts = mutableStateListOf<Workout>()
    val workouts: List<Workout> = _workouts

    init {
        val workouts = storage.loadWorkouts().map { workout ->
            workout.copy(exercises = workout.exercises.toMutableStateList())
        }
        _workouts.addAll(workouts)
    }

    fun getWorkoutFromId(id: String?) : Workout? {
        return _workouts.find { it.id.toString() == id }
    }

    fun addWorkout(workout: Workout) {
        _workouts.add(workout)
        saveWorkouts()
    }

    fun deleteWorkout(workout: Workout) {
        _workouts.remove(workout)
        saveWorkouts()
    }

    fun saveWorkouts() {
        storage.saveWorkouts(_workouts)
    }

    fun getAllExerciseNames() : List<String> {
        return _workouts.flatMap { it.exercises }.map { it.name }.distinct()
    }
}