package com.example.balanceverattempt.HomeNav;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.balanceverattempt.FirebaseDatabaseHelper;
import com.example.balanceverattempt.R;
import com.example.balanceverattempt.models.User;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private EditText editTextName, editTextAddress, editTextPhone, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button signUpButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        editTextName = view.findViewById(R.id.fullNameEditText);
        editTextAddress = view.findViewById(R.id.addressEditText);
        editTextPhone = view.findViewById(R.id.phoneEditText);
        editTextEmail = view.findViewById(R.id.emailEditText);
        editTextPassword = view.findViewById(R.id.passwordEditTextCreateAccount);
        editTextConfirmPassword = view.findViewById(R.id.confirmPasswordEditText);

        signUpButton = view.findViewById(R.id.signUpBtnSignUpFrag);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            //handle the already logged in user
        }
    }

    public void goToSignInFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, new SignInFragment(), "SIGN_IN_FRAGMENT").commit();
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Name required");
            editTextName.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            editTextAddress.setError("Address required");
            editTextAddress.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number required");
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            editTextPhone.setError("Enter a valid phone number");
            editTextPhone.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters long");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        User user = new User(
                name,
                address,
                phone,
                email,
                password
        );
        new FirebaseDatabaseHelper().addUser(user, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<User> users, List<String> keys) {

            }

            @Override
            public void DataIsInserted() {
                progressBar.setVisibility(View.GONE);
                goToSignInFragment();
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
        });
    }

}
