package com.roblebob.ultradianx.repository.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.roblebob.ultradianx.repository.model.AdventureDao
import com.roblebob.ultradianx.repository.model.AppDatabase

class DeleteAdventureWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams)  {

    private val mAdventureDao: AdventureDao

    init {
        mAdventureDao = AppDatabase.getInstance(context).adventureDao()
    }


    override fun doWork(): Result {
        val id = inputData.getInt("id", -1)
        if (id < 1) {
            return Result.failure()
        }
        mAdventureDao.loadAdventureById(id)?.let {
            mAdventureDao.delete(it)
            return Result.success()
        }
        return Result.failure()
    }
}