package com.example.balanceverattempt.LoggedInNav.controllers;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class EventbriteEventsController extends AsyncTask<String, Void, Void> {
    public static final String TAG = "EventbriteEventsContr";

    // Get events
    @Override
    protected Void doInBackground(String... params) {
        Log.d(TAG, "doInBackground: params: " + params);
        try {
            getEvents(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getEvents(String accessToken) throws IOException {
        HttpClient client = new DefaultHttpClient();
        String getURL = "https://www.eventbriteapi.com/v3/events";
        HttpGet httpGet = new HttpGet(getURL);
        httpGet.setHeader("Authorization", "Bearer " + accessToken);
        HttpResponse response = client.execute(httpGet);
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null) {
            Log.d(TAG, "getEvents: Response: " + EntityUtils.toString(resEntity));
        }
        // Eventbrite categories
        // Sports and Fitness = 108
        // Health and Wellness = 107
        // Science and Technology 102
        // Travel and Outdoor = 109
        // Religion and Spirituality = 114
        // Family and Education = 115
        // Fashion and Beauty = 106
        // Home & Lifestyle = 117
        // Music = 103
        // Business & Professional = 101
        // Food & Drink = 110
        // Community & Culture = 113
    }
}
