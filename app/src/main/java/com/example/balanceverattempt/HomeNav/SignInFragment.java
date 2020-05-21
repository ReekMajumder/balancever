package com.example.balanceverattempt.HomeNav;

import android.content.Intent;
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

import com.example.balanceverattempt.LoggedNav.LoggedInActivity;
import com.example.balanceverattempt.R;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

// TODO
// READ USER FROM HERE AND PASS A BUNDLE ONTO LOGGEDINACTIVITY
// PASS THE USER'S ID TO REFERENCE FROM ViewSchedule CLASS
public class SignInFragment extends Fragment {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "SignInFragment";
    private CallbackManager callbackManager;
    private TextView nameOrEmailTextView, passwordTextView;
    private ProgressBar signInProgressBar;
    private String password, email;

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private AccessTokenTracker accessTokenTracker;
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
//        setUpFirebaseListener();

        createRequest();

        googleSignInButton = view.findViewById(R.id.google_button);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signInProgressBar.setVisibility(View.VISIBLE);
                googleSignIn();
            }
        });

        // Normal Sign in button
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInProgressBar.setVisibility(View.VISIBLE);
                email = nameOrEmailTextView.getText().toString().trim();
                password = passwordTextView.getText().toString().trim();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signInProgressBar.setVisibility(View.GONE);
                            login();
                        } else {
                            signInProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
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

    private void login(){
        Intent intent = new Intent(getActivity(), LoggedInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("email", email);
        intent.putExtra("key", mAuth.getCurrentUser().getUid());
        startActivity(intent);
    }
    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
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

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            signInProgressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getActivity().getApplicationContext(), LoggedInActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Auth failed", Toast.LENGTH_SHORT);
                        }
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
