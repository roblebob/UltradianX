package com.roblebob.ultradianx.repository.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.roblebob.ultradianx.repository.model.Adventure
import com.roblebob.ultradianx.repository.model.AdventureDao
import com.roblebob.ultradianx.repository.model.AppDatabase

class CleanupWorker (context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams)  {

    private val mAdventureDao: AdventureDao

    init {
        mAdventureDao = AppDatabase.getInstance(context).adventureDao()
    }

    override fun doWork(): Result {
        mAdventureDao.cleanup()
        return Result.success()
    }
}