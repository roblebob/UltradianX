package com.roblebob.ultradianx.repository.worker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.roblebob.ultradianx.repository.model.Adventure
import com.roblebob.ultradianx.repository.model.AdventureDao
import com.roblebob.ultradianx.repository.model.AppDatabase

/**
 * This worker is used to add a new adventure to the database.
 * It checks if the title already exists, and if so, it fails.
 * Otherwise it adds the adventure to the database.
 *
 */
class AddAdventureWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val mAdventureDao: AdventureDao

    init {
        mAdventureDao = AppDatabase.getInstance(context).adventureDao()
    }

    override fun doWork(): Result {
        val candidate = Adventure(inputData)
        if (mAdventureDao.countAdventuresWithTitle(candidate.title) != 0) {
            Toast.makeText(applicationContext, "Failed, title already exists!", Toast.LENGTH_SHORT)
                .show()
            Log.e(TAG, "Failed, title already exists!")
            return Result.failure()
        }
        mAdventureDao.insert(candidate)
        return Result.success()
    }

    companion object {
        val TAG: String = AddAdventureWorker::class.java.simpleName
    }
}