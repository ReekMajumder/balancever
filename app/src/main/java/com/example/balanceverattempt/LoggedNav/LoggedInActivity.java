package com.example.balanceverattempt.LoggedNav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.balanceverattempt.HomeNav.ContactFragment;
import com.example.balanceverattempt.HomeNav.HomeFragment;
import com.example.balanceverattempt.HomeNav.MainActivity;
import com.example.balanceverattempt.HomeNav.SignInFragment;
import com.example.balanceverattempt.R;
import com.example.balanceverattempt.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class LoggedInActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "LoggedInActivity";
    private Toolbar loggedToolbar;
    private DrawerLayout drawer;
    private Menu toolbarMenu;
    private GoogleSignInAccount signInAccount;
    private String email, key;
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

        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (savedInstanceState == null) {
            openLoggedInFragment();
        }

        // Normal login
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("Normal login.");
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.child("email").getValue(String.class).equals(email)){
                        currentUser = new User(ds.child("name").getValue(String.class), ds.child("address").getValue(String.class),
                                            ds.child("phone").getValue(String.class), ds.child("email").getValue(String.class),
                                            ds.child("password").getValue(String.class));
                        currentUser.setId(key);
                        System.out.println(currentUser);
                        if (toolbarMenu != null && currentUser != null){
                            toolbarMenu.findItem(R.id.menuName).setTitle(currentUser.getName());
                        }
                        return;
                    }
                }
                System.out.println("EMAIL NOT FOUND");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null){
            currentUser = null;
        }
    }

    private void setUpNavBar() {
        NavigationView navigationView = findViewById(R.id.logged_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, loggedToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        System.out.println("onprepareoptions");
        return super.onPrepareOptionsMenu(menu);
    }

//    public void setProfilePic(Uri uri) throws IOException {
//        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//        MenuItem profilePic = findViewById(R.id.profilePic);
//        profilePic.setIcon(new BitmapDrawable(getResources(), bitmap));
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("oncreateoptions");
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
                Toast.makeText(this, "Name selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.SignOut:
                signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void signOut() {
        Log.d(TAG, "onClick: attempting to sign out the user");
        FirebaseAuth.getInstance().signOut();
        SignInFragment.mGoogleSignInClient.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_structure_day:
                openStructureDayFragment();
                break;
            case R.id.nav_view_schedule:
                openViewScheduleFragment();
                break;
            case R.id.nav_copy_schedule:
                openCopyScheduleFragment();
                break;
            case R.id.nav_check_your_score:
                openCheckYourScoreFragment();
                break;
            case R.id.nav_share_schedule:
                openShareYourScheduleFragment();
                break;
            case R.id.nav_explore_activities:
                openExploreActivitiesFragment();
                break;
            case R.id.nav_contact:
                openContactFragment();
                break;
            case R.id.nav_calendar:
                openTestCalendarFragment();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openTestCalendarFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.logged_fragment_container, new TestCalendarFragment(), "CALENDAR_FRAGMENT").commit();
    }

    public void strucDayBtnOnClick(View view){
        openStructureDayFragment();
    }

    public void ideasBtnOnClick(View view){
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

}
