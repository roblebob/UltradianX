package com.roblebob.ultradianx.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.repository.model.AppDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppRepository {
    public static final String TAG = AppRepository.class.getSimpleName();

    AdventureDao adventureDao;
    String mSrcUrl;

    public AppRepository(Context context) {
        adventureDao = AppDatabase.getInstance(context).adventureDao();
        mSrcUrl = context.getString(R.string.src_url);

    }

    public LiveData<List<Adventure>> getAdventureListLive() {
        return adventureDao.loadAdventureListLive();
    }

    /**
     * Insert a entities of the data model into the database
     * (overloaded methods, part of the integration process)
     */
    public void insert( Adventure adventure) {
        Executors.getInstance().diskIO().execute( () -> adventureDao.insert( adventure));
    }


    public void integrate() {
        Executors.getInstance().networkIO().execute( () -> {

            String result = "";

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
            }


            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    insert(new Adventure(
                                    jsonObject.getString("title"),
                                    jsonObject.getJSONArray("tags").toString(),
                                    jsonObject.getJSONArray("details").toString()
                            )
                    );
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error, while gathering json data", e);
            }
        });
    }
}
