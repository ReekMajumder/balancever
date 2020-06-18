package com.example.balanceverattempt.HomeNav;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.balanceverattempt.util.FirebaseDatabaseHelper;
import com.example.balanceverattempt.LoggedInNav.LoggedInActivity;
import com.example.balanceverattempt.R;
import com.example.balanceverattempt.models.User;
import com.example.balanceverattempt.LoggedInNav.controllers.GoogleCalendarUtil;
import com.facebook.login.widget.LoginButton;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SignInFragment extends Fragment {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "SignInFragment";
    private TextView nameOrEmailTextView, passwordTextView;
    private ProgressBar signInProgressBar;
    private String password, email;

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    public static GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignInButton;

    private Button logInButton;
    private LoginButton fbLogInButton;

    @Override
    public void onStart() {
        super.onStart();
        SignInFragment.mGoogleSignInClient.signOut();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        nameOrEmailTextView = view.findViewById(R.id.editText);
        passwordTextView = view.findViewById(R.id.passwordEditText);
        logInButton = view.findViewById(R.id.signInBtnInFrag);

        signInProgressBar = view.findViewById(R.id.signInProgressBar);
        signInProgressBar.setVisibility(View.GONE);
//        fbLogInButton = view.findViewById(R.id.fb_login_button);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Creating Google Sign in options
        createGoogleSignInOptions();

        googleSignInButton = view.findViewById(R.id.google_button);

        googleSignInButton.setOnClickListener(v -> {
            signInProgressBar.setVisibility(View.VISIBLE);
            googleSignIn();
        });

        // Normal Sign in button
        logInButton.setOnClickListener(v -> {
            signInProgressBar.setVisibility(View.VISIBLE);
            email = nameOrEmailTextView.getText().toString().trim();
            password = passwordTextView.getText().toString().trim();
            if (email.equals("")) {
                signInProgressBar.setVisibility(View.GONE);
                Snackbar.make(view, "ERROR: Email is empty.", Snackbar.LENGTH_SHORT).show();
                nameOrEmailTextView.setError("Email Required");
                nameOrEmailTextView.requestFocus();
            } else if (password.equals("")) {
                signInProgressBar.setVisibility(View.GONE);
                Snackbar.make(view, "ERROR: Password is empty.", Snackbar.LENGTH_SHORT).show();
                passwordTextView.setError("Password Required");
                passwordTextView.requestFocus();
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        signInProgressBar.setVisibility(View.GONE);
                        login();
                    } else {
                        signInProgressBar.setVisibility(View.GONE);
                        Snackbar.make(getView(), "Login Failed", Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signInProgressBar.setVisibility(View.GONE);
                        logInButton.setError("Invalid Email or Password.");
                        Snackbar.make(getView(), "Invalid Email or Password. Try again.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
//        callbackManager = CallbackManager.Factory.create();
//        fbLogInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "onSucess" + loginResult);
//                handleFacebookToken(loginResult.getAccessToken());
////                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "onError" + error);
//
//            }
//        });

//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    updateUI(user);
//                } else {
//                    updateUI(null);
//                }
//            }
//        };
//
//        accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                if (currentAccessToken == null) {
//                    mAuth.signOut();
//                }
//            }
//        };
        return view;
    }

    // Intent Handler for normal login
    private void login() {
        Intent intent = new Intent(getActivity(), LoggedInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("email", email);
        intent.putExtra("key", mAuth.getCurrentUser().getUid());
        intent.putExtra("google_account", "false");
        startActivity(intent);
    }

    // Invoked when this fragment is created: Initializing GoogleSignInOptions
    private void createGoogleSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id), true)
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    // After clicking the google sign in button
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount googleAccount = task.getResult(ApiException.class);
                // Starting alternate thread to get google credentials
                firebaseAuthWithGoogle(googleAccount.getIdToken(), googleAccount, googleAccount.getServerAuthCode());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Snackbar.make(getView(), "Google Sign in failed. Please Try again.", Snackbar.LENGTH_SHORT).show();
                Log.w(TAG, "Google sign in failed", e);
                signInProgressBar.setVisibility(View.GONE);
            }
        } else if (resultCode == -1) {
            System.out.println("Intent data: " + data.getExtras());
        }
    }

    // Registering google account with firebase authorization
    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount googleAccount, String serverAuthCode) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        // If Google Account does not exist in database, it is added.
                        // For storing events purposes so we don't consistently fetch events
                        new FirebaseDatabaseHelper().googleAccountExistsOrNot(googleAccount.getEmail(), new FirebaseDatabaseHelper.DataStatus() {
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
                            public void DataEventIsLoaded(List<Event> eventList) {
                            }

                            @Override
                            public void GoogleUserExists(boolean value) throws IOException, ExecutionException, InterruptedException, ParseException {
                                if (!value){
                                    creatingGoogleUserInDatabase(googleAccount);
                                }
                            }
                        });
                        signInProgressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(getActivity().getApplicationContext(), LoggedInActivity.class);
                        System.out.println("Firebase useruid: " + firebaseUser.getUid());
                        intent.putExtra("email", googleAccount.getEmail());
                        intent.putExtra("server_auth_code", serverAuthCode);
                        intent.putExtra("google_account", "true");
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(requireView(), "Authentication failed", Snackbar.LENGTH_LONG);
                    }
                });
    }

    private void creatingGoogleUserInDatabase(GoogleSignInAccount googleAccount) throws IOException, ExecutionException, InterruptedException, ParseException {
        System.out.println("Creating google user in database.");
        // Check if this user is already created
        User user = new User(
                googleAccount.getGivenName(),
                "Confidential",
                "Confidential",
                googleAccount.getEmail(),
                "Confidential"
        );
        /*
            1. Gets Google credentials
            2. Once credentials are received then the respective events are received.
         */
        List<com.google.api.services.calendar.model.Event> googleEventList = new GoogleCalendarUtil(googleAccount.getServerAuthCode()).execute().get();

        // Fetch google calendar events and put them into user
        for (com.google.api.services.calendar.model.Event event : googleEventList){
            String startDateString = event.getStart().getDateTime().toString();
            String endDateString = event.getEnd().getDateTime().toString();

            // Format of startDateString --> 2020-09-22T13:00:00.000-04:00
            // After T, time is shown in 24 hour format-12 hour format
            String[] splitStartD = startDateString.split("T");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateOfEvent = simpleDateFormat.parse(splitStartD[0]);
            String[] splitStartTime = splitStartD[1].split("-");

            String[] splitEndD = endDateString.split("T");
            String[] splitEndTime = splitEndD[1].split("-");

            String summary = event.getSummary().toString();
            System.out.println("Start date string: " + startDateString + "\nEnd date string: " + endDateString);
            System.out.println("Summary: " + summary);

            // For 24 hour format
            Event userEvent = new Event(Color.CYAN, dateOfEvent.getTime(),
                    summary+"/"+splitStartTime[0].substring(0, 5)+"/"+splitEndTime[0].substring(0, 5));
            System.out.println("User event: " + userEvent);
            user.addEvent(userEvent);
        }

        new FirebaseDatabaseHelper().addGoogleUser(user, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<HashMap<String, String>> users, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {
                Snackbar.make(requireView(), "Google User inserted in Database.", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }

            @Override
            public void DataEventIsLoaded(List<Event> eventList) {

            }

            @Override
            public void GoogleUserExists(boolean value) {

            }
        });
    }

    private void googleUpdateUI(FirebaseUser user) {
        startActivity(new Intent(getActivity(), LoggedInActivity.class));
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        if (account != null) {
            String name = account.getDisplayName();
            String givenName = account.getGivenName();
            String familyName = account.getFamilyName();
            String email = account.getEmail();
            String id = account.getId();
            Uri photo = account.getPhotoUrl();

            Toast.makeText(getActivity(), name + ", " + email, Toast.LENGTH_SHORT).show();
        }
    }

//    private void handleFacebookToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookToken" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "Sign In with credentials Successful");
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    updateUI(user);
//                } else {
//                    Log.d(TAG, "Sign In with credentials Failure", task.getException());
//                    Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            System.out.println(user.getDisplayName());
            if (user.getPhotoUrl() != null) {
                String photoUrl = user.getPhotoUrl().toString();
                photoUrl = photoUrl + "?type=large";
//                Picasso.get().load(photoUrl).into(mLogo);
            }
        } else {
            System.out.println("user is null");
        }
    }
}
