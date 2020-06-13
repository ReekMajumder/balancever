package com.example.balanceverattempt.HomeNav;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import com.example.balanceverattempt.R;
import com.google.android.material.snackbar.Snackbar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    WebView webView;

    String server_response;
    String accessToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((DrawerLocker) getActivity()).setDrawerEnabled(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        String clientSideUrl = "https://www.eventbrite.com/oauth/authorize?response_type=token&client_id=7LMRJ2OPMHAV74KM5Q&redirect_uri=balancever-8db5b.firebaseapp.com/__/auth/handler";
        String serverSideUrl = "https://www.eventbrite.com/oauth/authorize?response_type=code&client_id=7LMRJ2OPMHAV74KM5Q&redirect_uri=balancever-8db5b.firebaseapp.com/__/auth/handler";

        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setSupportMultipleWindows(true);
        webView.setVisibility(View.INVISIBLE);              // INVISIBLE
//        webView.loadUrl(serverSideUrl);

        if (webView.getVisibility() == View.VISIBLE) {
            webView.setWebViewClient(new WebViewClient() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                    if ("www.eventbrite.com".equals(Uri.parse(String.valueOf(request.getUrl())).getHost())) {
//                        // This is my website, so do not override; let my WebView load the page
//                        Log.d(TAG, "shouldOverrideUrlLoading: url:" + request.getUrl());
//                        return false;
//                    }
                    Log.d(TAG, "shouldOverrideUrlLoading: url: " + request.getUrl());
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.i(TAG, "URL: " + url);
                    if (url.startsWith("https://www.eventbrite.ca/oauth/balancever-8db5b.firebaseapp.com/__/auth/handler")) {
                        String[] splitUrl = url.split("code=");
                        String accessToken = splitUrl[1];
                        System.out.println("Access code: " + accessToken);
                        webView.setVisibility(View.INVISIBLE);
                        retrieveAccessToken(accessToken);
                    }
                    super.onPageFinished(view, url);
                }
            });
        }
        return view;
    }

    public void retrieveAccessToken(String accessToken) {
        Log.d(TAG, "retrieveAccessToken: " + accessToken);
        this.accessToken = accessToken;
        webView.setVisibility(View.INVISIBLE);
        new CallAPI().execute(accessToken);
    }

    public void getPrivateToken(String accessToken) {
        String request = "https://www.eventbrite.com/oauth/token";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(request);
        System.out.println("Code: " + accessToken);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(1);
            nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
            nameValuePairs.add(new BasicNameValuePair("client_id", "7LMRJ2OPMHAV74KM5Q"));
            nameValuePairs.add(new BasicNameValuePair("client_secret", "6RYBAD4AVHPKINVR3ZHBAJWWCPRDZPBIWBVHFVRMXGTBB5AK5G"));
            nameValuePairs.add(new BasicNameValuePair("code", accessToken));
            nameValuePairs.add(new BasicNameValuePair("redirect_uri", "balancever-8db5b.firebaseapp.com/__/auth/handler"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "Response body: " + responseBody);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error sending ID token to backend.", e);
        } catch (IOException e) {
            Log.e(TAG, "Error sending ID token to backend.", e);
        }
    }


    public class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            getPrivateToken(accessToken);
            return null;
        }

        private void getEvents(String accessToken) throws IOException {
            HttpClient client = new DefaultHttpClient();
            String getURL = "https://www.eventbriteapi.com/v3/categories/108";
            HttpGet httpGet = new HttpGet(getURL);
            httpGet.setHeader("Authorization", "Bearer " + accessToken);
            HttpResponse response = client.execute(httpGet);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                //parse response.
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
}
