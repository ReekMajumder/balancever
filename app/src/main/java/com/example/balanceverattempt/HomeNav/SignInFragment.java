package com.example.balanceverattempt.HomeNav;

import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.example.balanceverattempt.FirebaseDatabaseHelper;
import com.example.balanceverattempt.LoggedNav.LoggedInActivity;
import com.example.balanceverattempt.R;
import com.example.balanceverattempt.models.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

import com.google.api.services.calendar.Calendar;
import com.google.firebase.functions.FirebaseFunctionsException;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class SignInFragment extends Fragment {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "SignInFragment";
    private CallbackManager callbackManager;
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
        //SignInFragment.mGoogleSignInClient.signOut();
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
//        setUpFirebaseListener();

        // Create Google Sign in options
        createRequest();

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
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    signInProgressBar.setVisibility(View.GONE);
                    login();
                } else {
                    signInProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
            });
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

    // Normal Login (If user signed up)
    private void login() {
        Intent intent = new Intent(getActivity(), LoggedInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("email", email);
        intent.putExtra("key", mAuth.getCurrentUser().getUid());
        intent.putExtra("google_account", "false");
        startActivity(intent);
    }

    // Invoked when this fragment is created: Initializing GoogleSignInOptions
    private void createRequest() {
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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        // From googleSignIn() method
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //System.out.println("Server Auth code: " + account.getServerAuthCode());

                // Attempting to automate getting refresh token
                //GetRefreshToken();
                UpdateRefreshToken();
                // Create firebase authentication with google
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                ;
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void GetRefreshToken() throws IOException {
        String CLIENT_SECRET_FILE = "/path/to/client_secret.json"; // Be careful not to share this!
        String REDIRECT_URI = "/path/to/web_app_redirect"; // Can be empty if you don’t use web redirects
        // Exchange auth code for access token
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(), new FileReader(CLIENT_SECRET_FILE));
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://www.googleapis.com/oauth2/v4/token",
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret(),
//                        authCode,
                        REDIRECT_URI)
                        .execute();
        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        Long expiresInSeconds = tokenResponse.getExpiresInSeconds();
    }

    // Update Refresh Token
    private void UpdateRefreshToken() {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("functions/credentials"));
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println("JSONObject: " + obj);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    // Registering google account with firebase authorization
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        //gettingGoogleCalendarEvents(idToken);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getInstance().getCurrentUser();
                        // Geting key of google user
//                        String key = new FirebaseDatabaseHelper().addGoogleUser(user, new FirebaseDatabaseHelper.DataStatus() {
//                            @Override
//                            public void DataIsLoaded(List<User> users, List<String> keys) {
//
//                            }
//
//                            @Override
//                            public void DataIsInserted() {
//                                Toast.makeText(getActivity(), "Successfully added User", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void DataIsUpdated() {
//
//                            }
//
//                            @Override
//                            public void DataIsDeleted() {
//
//                            }
//
//                            @Override
//                            public void DataEventIsLoaded(List<Event> eventList) {
//
//                            }
//                        });
                        signInProgressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(getActivity().getApplicationContext(), LoggedInActivity.class);
                        System.out.println(user);
                        System.out.println("Google mAtuhId: " + user.getUid());
                        intent.putExtra("email", email);
                        intent.putExtra("key", user.getUid());
                        intent.putExtra("google_account", "true");
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getActivity(), "Auth failed", Toast.LENGTH_SHORT);
                    }
                });
    }

    private void gettingGoogleCalendarEvents(String event) {
        GoogleCalendarHandler googCal = new GoogleCalendarHandler();
        googCal.addMessage(event).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                        System.out.println("Error! Details: " + details);
                        System.out.println("Error! Code: " + code);
                    }
                    // ...
                }
                // ...
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

    @Override
    public void onStop() {
        super.onStop();
    }
}
