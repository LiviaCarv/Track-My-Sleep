package com.project.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.project.sleeptracker.database.SleepDatabaseDao
import com.project.sleeptracker.database.SleepNight
import com.project.sleeptracker.formatNights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private var tonight = MutableLiveData<SleepNight?>()

    private val nights = database.getAllNights()

    val nightsString = nights.map { nights ->
        formatNights(nights, application.resources)
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    fun onStartTracking() {
        viewModelScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(newNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(newNight)
        }
    }

    fun onStopTracking() {
        viewModelScope.launch {
            /* Nesse contexto, return@launch significa que a função launch será retornada imediatamente, encerrando a corrotina atual.
            útil para garantir que a corrotina seja encerrada se o valor de tonight for nulo,
            evitando assim erros quando não há um SleepNight válido para manipular. */

            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    private suspend fun update(oldNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(oldNight)
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }
}