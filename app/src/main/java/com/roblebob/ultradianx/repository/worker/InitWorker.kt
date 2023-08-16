package com.roblebob.ultradianx.repository.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.roblebob.ultradianx.R
import com.roblebob.ultradianx.repository.model.Adventure
import com.roblebob.ultradianx.repository.model.AdventureDao
import com.roblebob.ultradianx.repository.model.AppDatabase
import com.roblebob.ultradianx.repository.model.AppState
import com.roblebob.ultradianx.repository.model.AppStateDao
import com.roblebob.ultradianx.util.UtilKt.getRidOfMillis
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.time.Instant

class InitWorker internal constructor(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    private val mAdventureDao: AdventureDao
    private val mAppStateDao: AppStateDao
    private val mSrcUrl: String

    init {
        mAdventureDao = AppDatabase.getInstance(appContext).adventureDao()
        mAppStateDao = AppDatabase.getInstance(appContext).appStateDao()
        mSrcUrl = appContext.getString(R.string.src_url)
    }

    override fun doWork(): Result {
        if (mAdventureDao.countAdventures() == 0) {

            val result: String = try {

                val client = OkHttpClient()
                val request: Request = Builder() .url( mSrcUrl) .build()
                val response = client .newCall( request) .execute()
                
                response.body.string()

            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "Error, while gathering request's response", e)
                return Result.failure()
            }
            
            try {
                val jsonArray = JSONArray(result)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    // title
                    val title = jsonObject.getString("title")

                    // tags
                    val tag = jsonObject.getString("tag")

                    // details
                    val details = ArrayList<String>()
                    val jsonArrayDetails = jsonObject.getJSONArray("details")
                    for (ii in 0 until jsonArrayDetails.length()) {
                        details.add(jsonArrayDetails.getString(ii))
                    }

                    // target in hole minutes
                    val target = jsonObject.getInt("target")
                    val now = getRidOfMillis(Instant.now().toString())
                    mAdventureDao.insert( Adventure(false, title, tag, details, "", 17.0, now!!, now, target))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e(TAG, "Error, while gathering json data", e)
                return Result.failure()
            }
            mAppStateDao.insert(AppState("initialRun", "run before"))
        } else {
            Log.e(TAG, "Already have data, not running")
        }
        return Result.success()
    }

    companion object {
        val TAG: String = InitWorker::class.java.simpleName
    }
}