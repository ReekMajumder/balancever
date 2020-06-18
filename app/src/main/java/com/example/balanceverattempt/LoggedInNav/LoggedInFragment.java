package com.example.balanceverattempt.LoggedInNav;

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

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoggedInFragment extends Fragment {

    public static final String TAG = "LoggedInFragment";

    private WebView webView;
    private EventbriteHandler eventbriteHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logged_in, container, false);

        eventbriteHandler = new EventbriteHandler();
        webView = view.findViewById(R.id.webView);

        Log.d(TAG, "onCreateView: current user: " + LoggedInActivity.getCurrentUser());
        // If access token does not exist in the current user then do this
        if (!LoggedInActivity.getCurrentUser().getEventbriteAccessToken().equals("")) {
            // Get events using a separate async task
            eventbriteHandler.setAccessToken(LoggedInActivity.getCurrentUser().getEventbriteAccessToken());

            eventbriteHandler.getEventsWithToken();
        } else {
            setUpWebView(view);
        }
        return view;
    }

    // For Eventbrite API Single-Sign-on
    public void setUpWebView(View view) {
        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(eventbriteHandler.getServerSideUrl());

        if (webView.getVisibility() == View.VISIBLE) {
            webView.setWebViewClient(new WebViewClient() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Log.d(TAG, "shouldOverrideUrlLoading: url: " + request.getUrl());
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.i(TAG, "URL: " + url);
                    // Retrieves Code once signed in
                    if (url.startsWith("https://www.eventbrite.ca/oauth/balancever-8db5b.firebaseapp.com/__/auth/handler")) {
                        String[] splitUrl = url.split("code=");
                        String accessToken = splitUrl[1];
                        System.out.println("Code: " + accessToken);
                        webView.setVisibility(View.INVISIBLE);
                        // Retrieving access token from code
                        try {
                            eventbriteHandler.retrieveAccessToken(accessToken, webView);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    super.onPageFinished(view, url);
                }
            });
        }
    }
}
