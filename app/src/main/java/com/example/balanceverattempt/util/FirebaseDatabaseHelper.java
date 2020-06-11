package com.example.balanceverattempt.util;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.balanceverattempt.models.User;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
    private FirebaseAuth mAuth;
    private List<Event> eventList = new ArrayList<>();


    public interface DataStatus {
        void DataIsLoaded(List<HashMap<String, String>> users, List<String> keys);

        void DataIsInserted();

        void DataIsUpdated();

        void DataIsDeleted();

        void DataEventIsLoaded(List<Event> eventList);

        void GoogleUserExists(boolean value) throws IOException, ExecutionException, InterruptedException, ParseException;
    }

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
    }

    public void googleAccountExistsOrNot(String googleEmail, DataStatus dataStatus) {

        mReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    HashMap<String, String> user = (HashMap<String, String>) keyNode.getValue();
                    if (user.get("email").equals(googleEmail)){
                        System.out.println(user.get("email"));
                        try {
                            dataStatus.GoogleUserExists(true);
                        } catch (IOException | InterruptedException | ExecutionException | ParseException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                try {
                    dataStatus.GoogleUserExists(false);
                } catch (IOException | ExecutionException | InterruptedException | ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    // Reading events from user and putting events read into "eventList"
    // for the coder to be able to use the eventList in database
    public void readEventsFromUser(String key, final DataStatus dataStatus) {
        mReferenceUsers.child(key).child("eventEventList").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot eventNode : dataSnapshot.getChildren()) {
                    // eventNode represents the children of eventEventList in database
                    // eventNode needs to be converted to an Event Object
                    int color = ((Long) eventNode.child("color").getValue()).intValue();
                    String data = (String) eventNode.child("data").getValue();
                    Long timeInMillis = (Long) eventNode.child("timeInMillis").getValue();
                    Event event = new Event(color, timeInMillis, data);
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

    public String addGoogleUser(User user, final DataStatus dataStatus) {
        // Might be redundant
        String key = mReferenceUsers.push().getKey();
        user.setId(key);
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
