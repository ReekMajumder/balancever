package com.example.balanceverattempt.LoggedInNav;

import android.view.View;
import android.webkit.WebView;

import com.example.balanceverattempt.LoggedInNav.controllers.EventbriteController;
import com.example.balanceverattempt.LoggedInNav.controllers.EventbriteEventsController;

import java.util.concurrent.ExecutionException;


public class EventbriteHandler{
    public static final String TAG = "EventbriteHandler";

    // Response type = token
    private String clientSideUrl = "https://www.eventbrite.com/oauth/authorize?response_type=token&client_id=7LMRJ2OPMHAV74KM5Q&redirect_uri=balancever-8db5b.firebaseapp.com/__/auth/handler";
    // Response type = code
    private String serverSideUrl = "https://www.eventbrite.com/oauth/authorize?response_type=code&client_id=7LMRJ2OPMHAV74KM5Q&redirect_uri=balancever-8db5b.firebaseapp.com/__/auth/handler";

    private String accessToken;
    private String code;

    public EventbriteHandler(){
    }

    public void retrieveAccessToken(String code, WebView webView) throws ExecutionException, InterruptedException {
        this.code = code;
        webView.setVisibility(View.INVISIBLE);
        // Once code is received -> POST request to get access token
        EventbriteController eventbriteController = new EventbriteController(code);
        accessToken = eventbriteController.execute().get();
        // Retrieving events
        getEventsWithToken();
    }

    public void getEventsWithToken(){
        new EventbriteEventsController().execute(accessToken);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getServerSideUrl() {
        return serverSideUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getCode() {
        return code;
    }

}
