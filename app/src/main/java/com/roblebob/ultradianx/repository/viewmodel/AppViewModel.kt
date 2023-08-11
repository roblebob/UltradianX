package com.roblebob.ultradianx.repository.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.roblebob.ultradianx.repository.model.Adventure
import com.roblebob.ultradianx.repository.model.AdventureDao
import com.roblebob.ultradianx.repository.model.AppDatabase
import com.roblebob.ultradianx.repository.model.AppStateDao
import com.roblebob.ultradianx.repository.worker.AddAdventureWorker
import com.roblebob.ultradianx.repository.worker.InitWorker
import com.roblebob.ultradianx.repository.worker.RefreshWorker

class AppViewModel(application: Application, private val savedStateHandle: SavedStateHandle?) : ViewModel() {
    private val mWorkManager: WorkManager
    private val adventureDao: AdventureDao
    private val appStateDao: AppStateDao

    init {
        adventureDao = AppDatabase.getInstance(application.applicationContext).adventureDao()
        appStateDao = AppDatabase.getInstance(application.applicationContext).appStateDao()
        mWorkManager = WorkManager.getInstance(application)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                return AppViewModel(
                    application,
                    savedStateHandle
                ) as T
            }
        }
    }


    val adventureListLive: LiveData<List<Adventure>>
        get() = adventureDao.loadAdventureListLive()
    val adventureIdListLive: LiveData<List<Int>>
        get() = adventureDao.loadAdventureIdListLive()

    fun getAdventureByIdLive(id: Int): LiveData<Adventure> {
        return adventureDao.loadAdventureByIdLive(id)
    }

    fun initialRun() {
        mWorkManager.enqueue(OneTimeWorkRequest.from(InitWorker::class.java))
    }

    fun refresh(data: Data?) {
        if (data == null) {
            mWorkManager.enqueue(OneTimeWorkRequest.from(RefreshWorker::class.java))
        } else {
            mWorkManager.enqueue(
                OneTimeWorkRequest.Builder(RefreshWorker::class.java)
                    .setInputData(data)
                    .build()
            )
        }
    }

    fun refreshAll(data: Data?) {
        if (data == null) {
            mWorkManager.enqueue(OneTimeWorkRequest.from(RefreshWorker::class.java))
        } else {
            mWorkManager.beginWith(OneTimeWorkRequest.from(RefreshWorker::class.java))
                .then(
                    OneTimeWorkRequest.Builder(RefreshWorker::class.java)
                        .setInputData(data)
                        .build()
                )
                .enqueue()
        }
    }

    fun addAdventure(data: Data) {
        mWorkManager.enqueue(
            OneTimeWorkRequest.Builder(AddAdventureWorker::class.java)
                .setInputData(data)
                .build()
        )
    }


}