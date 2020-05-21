package com.example.balanceverattempt;

import android.graphics.Color;
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
        mReferenceUsers.child(key).child("eventList").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot eventNode : dataSnapshot.getChildren()) {
                    // Event node cannot be casted to a Map.Entry<Date, Event>
                    // Must get individual children, not the pair, and create the pair using the individual event and date, and then add it to eventList

                    // PROBLEM: eventNode.getValue() cannot get casted to Map.Entry<Date, Event>
                    //      Possible solution: Cast to HashMap and use a stream to filter out objects
//                    HashMap<String, String> map = (HashMap<String, String>) eventNode.getChildren();
                    if (eventNode.child("value").hasChildren()) {
                        Iterator<DataSnapshot> iter = eventNode.child("value").getChildren().iterator();
                        while (iter.hasNext()) {
                            DataSnapshot snap = iter.next();
                            System.out.println("Snap:" + snap);
                            System.out.println(snap.getValue());
//                            if (snap.getKey().toString().equals("data")) {
//                                System.out.println(snap.getValue());
//                                data = (String) snap.getValue();
//                                System.out.println(data);
//                            } else if (snap.getKey().equals("timeInMillis")) {
//                                System.out.println(snap.getValue());
//                                System.out.println(snap.getClass());
//                                timeInMillis = (Long) Objects.requireNonNull(snap.getValue());
//                            }
                            String data = (String) snap.child("data").getValue();
                            Long timeInMillis = (Long) snap.child("timeInMillis").getValue();
                            Event event = new Event(Color.argb(255, 169, 68, 65), timeInMillis, data);
                            System.out.println("Event: " + event);
                            eventList.add(event);
                        }
//                        Event event = new Event(color, timeInMillis, data);
//                        System.out.println(event);
//                        eventList.add(event);
                    }
                }
                dataStatus.DataEventIsLoaded(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addUser(User user, final DataStatus dataStatus) {
        String key = mReferenceUsers.push().getKey();
        mReferenceUsers.child(key).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsInserted();
                    }
                });
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
