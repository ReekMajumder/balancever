package com.example.balanceverattempt.LoggedInNav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanceverattempt.util.FirebaseDatabaseHelper;
import com.example.balanceverattempt.HomeNav.ContactFragment;
import com.example.balanceverattempt.HomeNav.MainActivity;
import com.example.balanceverattempt.HomeNav.SignInFragment;
import com.example.balanceverattempt.R;
import com.example.balanceverattempt.models.LoggedNavBarItem;
import com.example.balanceverattempt.models.User;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class LoggedInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoggedInActivity";
    private Toolbar loggedToolbar;
    private DrawerLayout drawer;
    private Menu toolbarMenu;
    private GoogleSignInAccount signInAccount;
    private String email, key, isGoogleAccount;
    private static User currentUser;
    private Button structDayButton, ideasButton;

    private FirebaseDatabase mDatabase;
    private static DatabaseReference userRef;
    private static final String USERS = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        structDayButton = findViewById(R.id.structureDayBtn);
        ideasButton = findViewById(R.id.ideasActivitiesButton);
        drawer = findViewById(R.id.logged_drawer_layout);
        loggedToolbar = findViewById(R.id.logged_toolbar);
        setSupportActionBar(loggedToolbar);
        getSupportActionBar().setTitle("");
        setUpNavBar();

        mDatabase = FirebaseDatabase.getInstance();
        userRef = mDatabase.getReference(USERS);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        key = intent.getStringExtra("key");
        isGoogleAccount = intent.getStringExtra("google_account");

        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (savedInstanceState == null) {
            openLoggedInFragment();
        }

        // Handling the Current User when Logging in
        // Setting currentUser it's values
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isGoogleAccount.equals("false")) {
                    System.out.println(TAG + ": Normal login.");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("email").getValue(String.class).equals(email)) {
                            currentUser = new User(ds.child("name").getValue(String.class),
                                    ds.child("address").getValue(String.class),
                                    ds.child("phone").getValue(String.class),
                                    ds.child("email").getValue(String.class),
                                    ds.child("password").getValue(String.class));
                            currentUser.setId(key);
                            getEventsFromDatabase(currentUser.getId());
                            System.out.println(currentUser);
                            if (toolbarMenu != null && currentUser != null) {
                                toolbarMenu.findItem(R.id.menuName).setTitle(currentUser.getName());
                            }
                            break;
                        }
                    }
                } else if (isGoogleAccount.equals("true")) {
                    System.out.println(TAG + ": Google Login");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        System.out.println("Ds.child.email: " + ds.child("email").getValue(String.class)+", intent email: " + email);
                        if (ds.child("email").getValue(String.class).equals(email)) {
                            currentUser = new User(ds.child("name").getValue(String.class),
                                    ds.child("address").getValue(String.class),
                                    ds.child("phone").getValue(String.class),
                                    ds.child("email").getValue(String.class),
                                    ds.child("password").getValue(String.class));
                            currentUser.setId(ds.child("id").getValue(String.class));
                            getEventsFromDatabase(currentUser.getId());
                            System.out.println(TAG + ": Current Google User: " + currentUser);
                            if (toolbarMenu != null && currentUser != null) {
                                toolbarMenu.findItem(R.id.menuName).setTitle(currentUser.getName());
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Add events in database to current user
    public void getEventsFromDatabase(final String id) {
        new FirebaseDatabaseHelper().readEventsFromUser(id, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<HashMap<String, String>> users, List<String> keys) {
            }

            @Override
            public void DataIsInserted() {
            }

            @Override
            public void DataIsUpdated() {
            }

            @Override
            public void DataIsDeleted() {
            }

            @Override
            public void DataEventIsLoaded(List<com.github.sundeepk.compactcalendarview.domain.Event> eventList) {
                for (Event e : eventList) {
                    currentUser.addEvent(e);
                }
            }

            @Override
            public void GoogleUserExists(boolean value) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Making sure the currentUser is only instantiated in onCreate method
        if (currentUser != null) {
            currentUser = null;
        }
    }

    private void setUpNavBar() {
        setNavDrawerButtons();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, loggedToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void setNavDrawerButtons() {
        for (LoggedNavBarItem item : LoggedNavBarItem.values()) {
            TextView itemView = findViewById(item.getItemId());
            itemView.setOnClickListener(this);
        }
    }

//    public void setProfilePic(Uri uri) throws IOException {
//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//        MenuItem profilePic = findViewById(R.id.profilePic);
//        profilePic.setIcon(new BitmapDrawable(getResources(), bitmap));
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_dropdown_menu, menu);
        toolbarMenu = menu;
        if (signInAccount != null) {
            toolbarMenu.findItem(R.id.menuName).setTitle(signInAccount.getDisplayName());
//            try {
////                setProfilePic(signInAccount.getPhotoUrl());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuName:
                return true;
            case R.id.accountSettings:

            case R.id.SignOut:
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut() {
        Log.d(TAG, "Sign out: attempting to sign out the user");
        FirebaseAuth.getInstance().signOut();
        SignInFragment.mGoogleSignInClient.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void openTestCalendarFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new TestCalendarFragment(), "CALENDAR_FRAGMENT").commit();
    }

    public void strucDayBtnOnClick(View view) {
        openStructureDayFragment();
    }

    public void ideasBtnOnClick(View view) {
        openExploreActivitiesFragment();
    }

    private void openLoggedInFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new LoggedInFragment(), "LOG_IN_FRAGMENT").commit();
    }

    private void openContactFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new ContactFragment(), "CONTACT_FRAGMENT").commit();
    }

    private void openExploreActivitiesFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new ExploreActivitiesFragment(), "EXPLORE_ACTIVITIES_FRAGMENT").commit();
    }

    private void openShareYourScheduleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new ShareYourScheduleFragment(), "SHARE_YOUR_SCHEDULE_FRAGMENT").commit();
    }

    private void openCheckYourScoreFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new CheckYourScoreFragment(), "CHECK_YOUR_SCORE_FRAGMENT").commit();
    }

    private void openCopyScheduleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new CopyScheduleFragment(), "COPY_SCHEDULE_FRAGMENT").commit();
    }

    private void openViewScheduleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new ViewScheduleFragment(), "VIEW_SCHEDULE_FRAGMENT").commit();
    }

    private void openStructureDayFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new StructureDayFragment(), "STRUCTURE_DAY_FRAGMENT").commit();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        LoggedInActivity.currentUser = currentUser;
    }

    @Override
    public void onClick(View v) {
        switch (LoggedNavBarItem.fromViewId(v.getId())) {
            case STRUCTURE_YOUR_DAY:
                openStructureDayFragment();
                break;
            case VIEW_SCHEDULE:
                openViewScheduleFragment();
                break;
            case COPY_SCHEDULE:
                openCopyScheduleFragment();
                break;
            case CHECK_YOUR_SCORE:
                openCheckYourScoreFragment();
                break;
            case SHARE_YOUR_SCHEDULE:
                openShareYourScheduleFragment();
                break;
            case EXPLORE_ACTIVITIES:
                openExploreActivitiesFragment();
                break;
            case CONTACT:
                openContactFragment();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }
}
