package com.example.balanceverattempt.LoggedInNav.controllers;

import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
    This class handles the retrieval of the Google Calendar API.
    Requirements for use: Server Authentication code of Google Sign in Account
        Once given server authentication code, it uses the code to get the google credentials
        for that specific account. Then it receives Google Calendar events from that specific account
        through the credentials.
 */
public class GoogleCalendarUtil extends AsyncTask<Void, Void, List<Event>> {

    public static final String TAG = "GoogleCalendarUtil";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private String serverAuthCode;

    public GoogleCalendarUtil(String serverAuthCode) {
        this.serverAuthCode = serverAuthCode;
    }

    protected List<Event> doInBackground(Void... voids) {
        try {
            // Returning eventList
            return getGoogleCredentials();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Event> getGoogleCredentials() throws IOException {
        System.out.println(TAG + ": Getting Google Credentials:");
        String REDIRECT_URI = "https://developers.google.com/oauthplayground"; // Can be empty if you don’t use web redirects
        // Exchange auth code for access token
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JSON_FACTORY,
                        "https://oauth2.googleapis.com/token",
                        "409031974447-86iodv6f7bamoplnhl7007h4id6dpup0.apps.googleusercontent.com",
                        "aWt1RDeY_c5p_NHFqN7_LF0j",
                        serverAuthCode,
                        REDIRECT_URI)  // Specify the same redirect URI that you use with your web
                        // app. If you don't have a web version of your app, you can
                        // specify an empty string.
                        .execute();

        String accessToken = tokenResponse.getAccessToken();
        // Use access token to call API
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        return getGoogleCalendarEvents(credential);
    }

    public List<Event> getGoogleCalendarEvents(GoogleCredential credential) throws IOException {
        Calendar service = new Calendar.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), JSON_FACTORY, credential)
                .setApplicationName("Google Calendar")
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(50)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
        return items;
    }

    // TODO:
    public void addEventToGoogleCal(Event event){
    }
}
