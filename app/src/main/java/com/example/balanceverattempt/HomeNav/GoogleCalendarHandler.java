package com.example.balanceverattempt.HomeNav;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class GoogleCalendarHandler {
    private static FirebaseFunctions mFunctions;

    public GoogleCalendarHandler(){
        mFunctions = FirebaseFunctions.getInstance();
    }

    public static Task<String> addMessage(String txt){
        Map<String, Object> data = new HashMap<>();

        data.put("text", txt);
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }


}
