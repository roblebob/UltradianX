package com.roblebob.ultradianx.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.model.Adventure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateWorker extends Worker {
    public static final String TAG = UpdateWorker.class.getSimpleName();

    private final AdventureDao mAdventureDao;
    private final String mSrcUrl;

    UpdateWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        mAdventureDao = AppDatabase.getInstance(appContext).adventureDao();
        mSrcUrl = appContext.getString(R.string.src_url);
    }


    @NonNull
    @Override
    public Result doWork() {

        String result;

        try {
            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(mSrcUrl)
                    .build();

            Response response = client.newCall(request).execute();


            result = response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,  "Error, while gathering response" , e);
            return Result.failure();
        }


        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                mAdventureDao.insert(new Adventure(
                                jsonObject.getString("title"),
                                jsonObject.getJSONArray("tags").toString(),
                                jsonObject.getJSONArray("details").toString(),
                                jsonObject.getJSONArray("links").toString()
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error, while gathering json data", e);
            return Result.failure();
        }


        return Result.success();
    }
}
