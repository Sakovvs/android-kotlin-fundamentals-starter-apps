/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    private var viewModelJob = Job()        //viewModelJob позволяет отменить все корутины, запущенные этой viewModel, когда viewModel больше не используется и уничтожена.

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)           //определяет, в каком потоке будет выполняться корутина + должен знать о Job

    private val nights = database.getAllNights()

    val nightsString = Transformations.map(nights!!) { nights ->
        formatNights(nights, application.resources)
    }

    private val tonight = MutableLiveData<SleepNight?>()

    private val _navigationToSleepQuality = MutableLiveData<SleepNight>()
    val navigationToSleepQuality: LiveData<SleepNight>
        get() = _navigationToSleepQuality

    private var _showSnackBarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackBarEvent


    val startButtonVisible = Transformations.map(tonight) {
        it == null
    }
    val stopButtonVisible = Transformations.map(tonight) {
        it != null
    }
    val clearButtonVisible = Transformations.map(nights!!) {
        it?.isNotEmpty()
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            //launch - запуск
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

    fun doneNavigating() {
        _navigationToSleepQuality.value = null
    }

    fun doneShowingSnackBar() {
        _showSnackBarEvent.value = false
    }

    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigationToSleepQuality.value = oldNight
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
            _showSnackBarEvent.value = true
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }


    private suspend fun update(oldNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(oldNight)
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    override fun onCleared() {              //вызывается при уничтожении viewModel
        super.onCleared()
        viewModelJob.cancel()
    }
}

