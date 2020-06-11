package com.example.balanceverattempt.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.auth.AuthUI;


import java.util.Arrays;

public class FirebaseUtil {

    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
//    public static ArrayList<Person> mPersons;
    private static final int RC_SIGN_IN = 123;
    private static Fragment caller;

    private FirebaseUtil() {
    }

    public static void openFbReference(String ref, final Fragment callerActivity) {

        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    // Not Signed In
                    if (firebaseAuth.getCurrentUser() == null) {
                    }
                }
            };
        }
//        mPersons = new ArrayList<Person>(); // maybe
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    private static void signIn() {
//        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
//                .Builder(R.layout.fragment_signin)
//                .setGoogleButtonId(R.id.g_btn)
//                .setFacebookButtonId(R.id.fb_btn).build();

//                .setTosAndPrivacyPolicyId(R.id.baz)

        // Choose authentication providers
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
//        caller.startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAuthMethodPickerLayout(customLayout)
////                        .setAvailableProviders(providers)
//                        .build(),
//                RC_SIGN_IN);
    }

    public static void fbSignIn() {
        AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.FacebookBuilder()
                .setPermissions(Arrays.asList("user_friends"))
                .build();

        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(facebookIdp))
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }


}
