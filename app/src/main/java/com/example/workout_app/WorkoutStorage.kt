package com.example.workout_app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class WorkoutStorage(private val context: Context) {
    private val gson = Gson()
    private val filename = "workouts.json"

    fun loadWorkouts(): MutableList<Workout> {
        val file = File(context.filesDir, filename)
        return if (file.exists()) {
            try {
                val json = file.readText()
                val type = object : TypeToken<MutableList<Workout>>() {}.type
                gson.fromJson(json, type) ?: mutableListOf()
            } catch (e: IOException) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
    }

    fun saveWorkouts(workouts: MutableList<Workout>) {
        try {
            val json = gson.toJson(workouts)
            File(context.filesDir, filename).writeText(json)
        } catch (e: IOException) {
            // Handle error
        }
    }
}