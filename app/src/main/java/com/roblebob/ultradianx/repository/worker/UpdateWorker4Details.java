package com.roblebob.ultradianx.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;
import com.roblebob.ultradianx.repository.model.Detail;
import com.roblebob.ultradianx.repository.model.DetailDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateWorker4Details extends Worker {
    public static final String TAG = UpdateWorker.class.getSimpleName();

    private final DetailDao mDetailDao;
    private final String mSrcUrl;

    UpdateWorker4Details(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        mDetailDao = AppDatabase.getInstance(appContext).detailDao();
        mSrcUrl = appContext.getString(R.string.src_url4details);
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
            Log.e(TAG, "---->" + result);
            JSONArray jsonArray = new JSONArray(result);

            Log.e(TAG, "<----");
            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String adventure = jsonObject.getString("adventure");
                String subject = jsonObject.getString("subject");
                String content = jsonObject.getString("content");

                Log.e(TAG, adventure + "  " + subject + "  " + content );

                mDetailDao.insert(new Detail( adventure, subject, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error, while gathering json data", e);
            return Result.failure();
        }


        return Result.success();
    }
}
