package com.roblebob.ultradianx.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.model.Adventure;

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
                Log.e(TAG,  "   Error, while gathering response" , e);
            }

            Log.e(TAG, result);

            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Log.e(TAG, jsonObject.getString("title"));

                    insert(new Adventure(
                                    i + 1,
                                    jsonObject.getString("title"),
                                    jsonObject.getJSONArray("tags").toString(),
                                    jsonObject.getJSONArray("descriptions").toString(),
                                    jsonObject.getJSONArray("links").toString()
                            )
                    );
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error, while gathering json data");
            }
        });
    }







    /**
     * This method returns the entire result from the HTTP response, using the third party library
     * OkHttp (part of the integration process)
     *
     * @param urlString The URL to fetch the HTTP response from (as a String and NOT as URL).
     * @return The contents of the HTTP response.
     * @throws IOException ...
     */

    public static String getResponseFromHttpUrl_OkHttp(String urlString)  throws IOException {

        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlString)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}
