package com.project.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.project.sleeptracker.database.SleepDatabaseDao

class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) : AndroidViewModel(application) {}