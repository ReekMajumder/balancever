package com.example.balanceverattempt.LoggedInNav.controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.balanceverattempt.LoggedInNav.EventbriteHandler;
import com.example.balanceverattempt.LoggedInNav.LoggedInActivity;
import com.example.balanceverattempt.models.User;
import com.example.balanceverattempt.util.FirebaseDatabaseHelper;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventbriteController extends AsyncTask<String, String, String> {
    public static final String TAG = "EventbriteController";

    private String code;

    public EventbriteController(String code) {
        this.code = code;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
//        return getPrivateToken(code);
        String accessToken = getPrivateToken(code);
        Log.d(TAG, "doInBackground: accessToken: " + accessToken);
        return accessToken;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: Access token: " + s);
        // Add access token to database of current user
        User user = LoggedInActivity.getCurrentUser();
        user.setEventbriteAccessToken(s);
        new FirebaseDatabaseHelper().updateUser(user.getId(), user, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<HashMap<String, String>> users, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                Log.d(TAG, "DataIsUpdated: Access token added to user in database.");
            }

            @Override
            public void DataIsDeleted() {

            }

            @Override
            public void DataEventIsLoaded(List<Event> eventList) {

            }

            @Override
            public void GoogleUserExists(boolean value) throws IOException, ExecutionException, InterruptedException, ParseException {

            }
        });
    }

    // Sends a POST request to https://www.eventbrite.com/oauth/token
    // in order to receieve an access token
    /*
    POST request:
        grant_type = authorization_code
        client_id = APP_KEY
        client_secret = SECRET
        code = CODE
        redirect_uri = REDIRECT_URI
    */
    public String getPrivateToken(String code) {
        String request = "https://www.eventbrite.com/oauth/token";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(request);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(1);
            nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
            nameValuePairs.add(new BasicNameValuePair("client_id", "7LMRJ2OPMHAV74KM5Q"));
            nameValuePairs.add(new BasicNameValuePair("client_secret", "6RYBAD4AVHPKINVR3ZHBAJWWCPRDZPBIWBVHFVRMXGTBB5AK5G"));
            nameValuePairs.add(new BasicNameValuePair("code", code));
            nameValuePairs.add(new BasicNameValuePair("redirect_uri", "balancever-8db5b.firebaseapp.com/__/auth/handler"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            final String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(responseBody);
            String access_token = jsonResponse.getString("access_token");
            return access_token;
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error sending ID token to backend.", e);
        }
        return null;
    }
}
