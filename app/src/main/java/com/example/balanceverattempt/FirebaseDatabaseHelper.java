package com.example.balanceverattempt;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.balanceverattempt.models.User;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
    private FirebaseAuth mAuth;
    private List<User> users = new ArrayList<>();
    private List<Event> eventList = new ArrayList<>();


    public interface DataStatus {
        void DataIsLoaded(List<User> users, List<String> keys);

        void DataIsInserted();

        void DataIsUpdated();

        void DataIsDeleted();

        void DataEventIsLoaded(List<Event> eventList);
    }

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
    }

    public void readUsers(final DataStatus dataStatus) {
        mReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    User user = keyNode.getValue(User.class);
                    users.add(user);
                }
                dataStatus.DataIsLoaded(users, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readEventsFromUser(String key, final User user, final DataStatus dataStatus) {
        mReferenceUsers.child(key).child("eventEventList").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot eventNode : dataSnapshot.getChildren()) {
                    // eventNode represents the children of eventEventList in database
                    // eventNode needs to be converted to an Event Object
                    System.out.println(eventNode);
                    int color = ((Long) eventNode.child("color").getValue()).intValue();
                    String data = (String) eventNode.child("data").getValue();
                    Long timeInMillis = (Long) eventNode.child("timeInMillis").getValue();
//                    System.out.println("Color: " + color);
//                    System.out.println("Data: " + data);
//                    System.out.println("TimeInMillis: " + timeInMillis);
                    Event event = new Event(color, timeInMillis, data);
                    System.out.println("Event: " + event);
                    eventList.add(event);
                }
                dataStatus.DataEventIsLoaded(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addUser(User user, final DataStatus dataStatus) {
//        String key = mReferenceUsers.push().getKey();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mReferenceUsers.child(mAuth.getCurrentUser().getUid())
                                .setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        dataStatus.DataIsInserted();
                                    }
                                })
                                .addOnFailureListener(e -> System.out.println("Adding user to database error."));
                    }
                })
                .addOnFailureListener(e -> System.out.println("Failed to create authorization for user."));
    }

    // Might be redundant
    public String addGoogleUser(FirebaseUser user, final DataStatus dataStatus) {
        String key = mReferenceUsers.push().getKey();
        mReferenceUsers.child(key).setValue(user)
                .addOnSuccessListener(aVoid -> dataStatus.DataIsInserted());
        return key;
    }

    public void updateUser(String key, User user, final DataStatus dataStatus) {
        mReferenceUsers.child(key).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsUpdated();
                    }
                });
    }

    public void deleteUser(String key, final DataStatus dataStatus) {
        mReferenceUsers.child(key).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsDeleted();
                    }
                });
    }
}
