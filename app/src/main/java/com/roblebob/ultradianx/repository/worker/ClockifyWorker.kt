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
import com.roblebob.ultradianx.repository.model.History
import com.roblebob.ultradianx.repository.model.HistoryDao
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class ClockifyWorker internal constructor(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    private val appStateDao: AppStateDao
    private val adventureDao: AdventureDao
    private val historyDao: HistoryDao
    private val client = OkHttpClient()
    private val MEDIA_TYPE: MediaType? = "application/json, charset=utf-8".toMediaTypeOrNull()

    init {
        val appDatabase = AppDatabase.getInstance(appContext)
        appStateDao = appDatabase.appStateDao()
        adventureDao = appDatabase.adventureDao()
        historyDao = appDatabase.historyDao()
    }

    override fun doWork(): Result {
        if (appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE) == null) {
            // TODO mechanism which creates a new clockify workspace if necessary
            appStateDao.insert( AppState( CLOCKIFY_WORKSPACE, "63ad4e7d46839705993badc2")) // workspace called UlradianX));
            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }


        val historyList = historyDao.loadEntireHistory()
        historyList.forEach(Consumer<History> { history: History ->
            val adventure = validate(adventureDao.loadAdventureById(history.adventureId))
            try {
                val request: Request = Builder()
                    .addHeader("content-type", "application/json")
                    .addHeader("X-Api-Key", applicationContext.getString(R.string.clockify_api_key))
                    .url("$API_BASE_ENDPOINT/workspaces/${appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE)}/time-entries")
                    .post("""{
"start": "${history.start}",
"projectId": "${adventure.clockify}",
"end": "${history.end}"}""".toRequestBody(MEDIA_TYPE))
                    .build()
                val response = client.newCall(request).execute()
                val responseHeaders = response.headers
                for (i in 0 until responseHeaders.size) {
                    Log.e(
                        TAG + " Header",
                        responseHeaders.name(i) + ": " + responseHeaders.value(i)
                    )
                }
                val result = response.body.string()
                //Log.e(TAG, "---->\n " + result.replace(",", "\n,"));
                historyDao.delete(history)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "Error in processing $adventure  (updating time entries)", e)
            }
        })
        return Result.success()
    }








    /**
     * function validating an adventure, by guaranteeing a valid clockify id for it
     *
     * @param adventure to be considered
     * @return adventure
     */
    private fun validate(adventure: Adventure): Adventure {

        // check if adventure has a clockify entry
        return if (adventure.clockify.isNotEmpty()) {
            adventure
        } else try {
            var response = client.newCall(
                Builder()
                    .addHeader("content-type", "application/json")
                    .addHeader("X-Api-Key", applicationContext.getString(R.string.clockify_api_key))
                    .url("$API_BASE_ENDPOINT/workspaces/${appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE)}/projects")
                    .build()
            ).execute()
            val jsonArray = JSONArray(response.body.string())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                if (jsonObject.getString("name") == adventure.title) {
                    adventure.clockify = jsonObject.getString("id")
                    adventureDao.update(adventure)
                    return adventure
                }
            }


            // create new project associated with the adventure at hand
            response = client.newCall(
                Builder()
                    .addHeader("content-type", "application/json")
                    .addHeader("X-Api-Key", applicationContext.getString(R.string.clockify_api_key))
                    .url("$API_BASE_ENDPOINT/workspaces/${appStateDao.loadValueByKey(CLOCKIFY_WORKSPACE)}/projects")
                    .post(("{\"name\": \"${adventure.title}\"}").toRequestBody(MEDIA_TYPE))
                    .build()
            ).execute()
            adventure.clockify = JSONObject(response.body.string()).getString("id")
            adventureDao.update(adventure)
            adventure
        } catch (e: IOException) {
            Log.e(TAG, "Error validating adventure " + adventure.title, e)
            adventure
        } catch (e: JSONException) {
            Log.e(TAG, "Error validating adventure " + adventure.title, e)
            adventure
        }


        // check if clockify has a project corresponding to the adventure title
    }

    companion object {
        val TAG: String = ClockifyWorker::class.java.simpleName
        const val CLOCKIFY_USER = "clockify_user_id"
        const val CLOCKIFY_WORKSPACE = "clockify_workspace"
        const val API_BASE_ENDPOINT = "https://api.clockify.me/api/v1"
        const val API_BASE_ENDPOINT_FOR_REPORTS = "https://reports.api.clockify.me/v1"
        const val API_BASE_ENDPOINT_FOR_TIME_OFF = "https://pto.api.clockify.me/v1"
    }
}