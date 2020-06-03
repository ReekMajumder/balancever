package com.example.balanceverattempt.HomeNav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.balanceverattempt.LoggedNav.TestCalendarFragment;
import com.example.balanceverattempt.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLocker {

    private DrawerLayout drawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //getEventsFromCalendar();

        if (savedInstanceState == null) {
            openHomeFragment();
        }
    }

    public void getEventsFromCalendar() {
        String URL = "https://us-central1-balancever-8db5b.cloudfunctions.net/getEvents";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        System.out.println("Get Events from Calendar: ");
        // JSON request
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    String eventId, eventSummary, eventDescription, eventStartTime, eventEndTime;
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject event = jsonArray.getJSONObject(i);
                                if (!event.isNull("id"))
                                    eventId = event.getString("id");
                                else
                                    eventId = "Empty";
                                if (!event.isNull("summary"))
                                    eventSummary = event.getString("summary");
                                else
                                    eventSummary = "Empty";
                                if (!event.isNull("description"))
                                    eventDescription = event.getString("description");
                                else
                                    eventDescription = "Empty";
                                if (!event.isNull("start"))
                                    eventStartTime = event.getJSONObject("start").getString("dateTime");
                                else
                                    eventStartTime = "Empty";
                                if (!event.isNull("end"))
                                    eventEndTime = event.getJSONObject("end").getString("dateTime");
                                else
                                    eventEndTime = "Empty";
                                System.out.println("Event id: " + eventId + "\n\tSummary: " + eventSummary + "\n\tDescription: " + eventDescription + "\n\tStart Time: " + eventStartTime + "\n\tEnd Time: " + eventEndTime);
//                                System.out.println(event);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(objectRequest);
    }

    public void addEventToCalenadr() {
        String URL = "https://us-central1-balancever-8db5b.cloudfunctions.net/addEventToCalendar";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("REST response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REST response", error.toString());
                    }
                }
        );

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Post Data: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Post Data: " + error);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("eventName", "Firebase Event");
                params.put("description", "Sample description");
                params.put("startTime", "2020-05-29T10:00:00");
                params.put("endTime", "2020-05-30T13:00:00");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return (headers != null || headers.isEmpty()) ? headers : super.getHeaders();
            }
        };

        requestQueue.add(stringRequest);
    }

    public void signInBtnOnClick(View view) {
        openSignInFragment();
    }

    public void signUpBtnOnClick(View view) {
        openSignUpFragment();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                openHomeFragment();
                break;
            case R.id.nav_signin:
                openSignInFragment();
                break;
            case R.id.nav_signup:
                openSignUpFragment();
                break;
            case R.id.nav_contact:
                openContactFragment();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openHomeFragment() {
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT").commit();
    }

    public void openSignInFragment() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, new SignInFragment(), "SIGN_IN_FRAGMENT").commit();
    }

    public void openSignUpFragment() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, new SignUpFragment(), "SIGN_UP_FRAGMENT").commit();
    }

    public void openContactFragment() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, new ContactFragment(), "CONTACT_FRAGMENT").commit();
    }

    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);

        if (enabled == false) {
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        toggle.syncState();
    }

}
