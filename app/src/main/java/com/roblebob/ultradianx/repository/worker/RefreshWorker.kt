package com.roblebob.ultradianx.repository.worker

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.roblebob.ultradianx.repository.model.Adventure
import com.roblebob.ultradianx.repository.model.AdventureDao
import com.roblebob.ultradianx.repository.model.AppDatabase
import com.roblebob.ultradianx.repository.model.History
import com.roblebob.ultradianx.repository.model.HistoryDao
import java.util.function.Consumer

/**
 * This worker is used to update the priority of the passive adventures.
 * It checks the last time the adventure was updated, and calculates the new priority, and finally
 * resets 'last' to now.
 */
class RefreshWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val mAdventureDao: AdventureDao
    private val mHistoryDao: HistoryDao
    private val mWorkManager: WorkManager
    private var mActiveCount = 0

    init {
        mAdventureDao = AppDatabase.getInstance(context).adventureDao()
        mHistoryDao = AppDatabase.getInstance(context).historyDao()
        mWorkManager = WorkManager.getInstance(context)
    }

    override fun doWork(): Result {
        val data = inputData
        val id = data.getInt("id", -1)
        

        if (id < 1) {
            // case: no valid id => refresh all instead
            
            
            mAdventureDao.loadAdventureList().forEach(Consumer { adventure1: Adventure? ->
                // TODO
                val adventure = Adventure(adventure1)
                adventure.refresh()
                mAdventureDao.update(adventure)
                if (adventure.isActive) {
                    mActiveCount++
                }
            })
            if (mActiveCount > 1) {
                Log.e(TAG, "---->  " + "WARNING!!! MORE THEN ONE IS ACTIVE!!!!!!!!!!")
            }
            
        } else {
            // case: valid id => refresh only this one, with the data provided
            
            val adventure = mAdventureDao.loadAdventureById(id)
            adventure.refresh()

            // if transition ...
            if (adventure.isActive &&  //  .. from active ...
                !data.getBoolean("active", true) // ... to passive
            ) {
                mHistoryDao.insert(
                    History(
                        adventure.id,
                        adventure.lastTimePassive,
                        adventure.lastTime
                    )
                )
                mWorkManager.enqueue(OneTimeWorkRequest.from(ClockifyWorker::class.java))
            }
            
            adventure.update(data)
            mAdventureDao.update(adventure)
        }
        return Result.success()
    }

    companion object {
        val TAG: String = RefreshWorker::class.java.simpleName
    }
}