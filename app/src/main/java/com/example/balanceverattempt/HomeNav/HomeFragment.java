package com.example.balanceverattempt.HomeNav;

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
import androidx.fragment.app.Fragment;

import com.example.balanceverattempt.R;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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

        String urlStr = "https://www.eventbrite.com/oauth/authorize?response_type=token&client_id=7LMRJ2OPMHAV74KM5Q&redirect_uri=balancever-8db5b.firebaseapp.com/__/auth/handler";

        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(urlStr);

        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "URL: " + url);
                if (url.startsWith("https://www.eventbrite.ca/oauth/balancever-8db5b.firebaseapp.com/__/auth/handler")) {
                    String[] splitUrl = url.split("access_token=");
                    String accessToken = splitUrl[1];
                    System.out.println("Access token: " + accessToken);
                    try {
                        retrieveAccessToken(accessToken);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
                super.onPageFinished(view, url);
            }
        });
        return view;
    }

    public void retrieveAccessToken(String accessToken) throws JSONException, IOException {
        Log.d(TAG, "retrieveAccessToken: " + accessToken);
        this.accessToken = accessToken;
        webView.setVisibility(View.INVISIBLE);
        new CallAPI().execute(accessToken);
    }

    public void getPrivateToken(String accessToken) throws IOException {
        String request = "https://www.eventbrite.com/oauth/authorize";

        /*
            POST request format:
                1. grant_type = authorization_code
                2. client_id = API_KEY
                3. client_secret = CLIENT_SECRET
                4. code = ACCESS_CODE
                5. REDIRECT_URI
         */
        String urlParameters = "grant_type=authorization_code&" +
                "client_id=7LMRJ2OPMHAV74KM5Q&" +
                "client_secret=6RYBAD4AVHPKINVR3ZHBAJWWCPRDZPBIWBVHFVRMXGTBB5AK5G&" +
                "code="+accessToken+"&" +
                "redirect_uri=https://www.eventbrite.com/oauth/token";
//                "redirect_uri=balancever-8db5b.firebaseapp.com/__/auth/handler";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        URL url = new URL(request);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }
        Log.d(TAG, "getPrivateToken: POST Response code: " + conn.getResponseCode());
        Log.d(TAG, "getPrivateToken: POST Response body: " + conn.getURL());


//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(url)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();

//        JSONObject jsonObject = new JSONObject();
////        jsonObject.put("header", "content-type: application/x-www-form-urlencoded");
//        jsonObject.put("grant_type", "authorization_code");
//        jsonObject.put("client_id", "2MLUARHUZAJ4RE4AS2");
//        jsonObject.put("client_secret", "5MAQOW5HZVSAIZ7UKPSBZSZLJ72XDBQS24TDL5PV2J5GJZLVT7");
//        // My 67qwert@gmail.com's access token
//        jsonObject.put("code", accessToken);
//        jsonObject.put("redirect_uri", "balancever-8db5b.firebaseapp.com/__/auth/handler");
//
//        final JsonObjectRequest jor = new JsonObjectRequest(
//                Request.Method.POST,
//                url, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("Response", String.valueOf(response));
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.i("Error Response", String.valueOf(error));
//                    }
//                }) {
//        };
//        requestQueue.add(jor);        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
    }


    public class CallAPI extends AsyncTask<String, String, String> {

        public CallAPI() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                getPrivateToken(accessToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
    // NEED TO GET ACCESS CODE FROM FIRST METHOD

    public void eventBriteAPI() throws IOException {
        String eventBriteAPIkey = "2MLUARHUZAJ4RE4AS2";
        String redirectURI = "balancever-8db5b.firebaseapp.com/__/auth/handler";
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.eventbrite.com/oauth/authorize?response_type=token&client_id=2MLUARHUZAJ4RE4AS2&redirect_uri=balancever-8db5b.firebaseapp.com/__/auth/handler"));
//        startActivity(browserIntent);

//        HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
        String response = "";
        try {
            URL url = new URL("https://www.eventbrite.com/oauth/authorize?response_type=token&client_id=2MLUARHUZAJ4RE4AS2&redirect_uri=balancever-8db5b.firebaseapp.com/__/auth/handler");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(os, "UTF-8"));
//            writer.write(getPostDataString(postDataParams));
//
//            writer.flush();
//            writer.close();
//            os.close();
            int responseCode = conn.getResponseCode();
            Log.i("Response Code", String.valueOf(responseCode));

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
            Log.i("Reponse", response);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
//            Map<String, List<String>> in = urlConnection.getHeaderFields();
//            int str = urlConnection.getResponseCode();
//
////            System.out.println("Response code: " + str + ": " + in);
////            urlConnection.getURL();
//            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
//            String result = s.hasNext() ? s.next() : "";
//            System.out.println("input stream: " + result);
////            System.out.println(result);
//
////            readStream(in);
//        } finally {
//            urlConnection.disconnect();
//        }
    }
}
