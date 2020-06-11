package com.example.balanceverattempt.LoggedInNav;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.balanceverattempt.R;

import java.util.Calendar;
import java.util.List;

import static android.provider.CalendarContract.Calendars;

public class TestCalendarFragment extends Fragment implements View.OnClickListener{

    public static final String[] EVENT_PROJECTION = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private Calendar calendar;

    private Context mContext;

    private Cursor cursor;
    private Button makeButton, getButton;
    private ListView eventListView;
    private List<String> events;
    private ArrayAdapter<String> arrayAdapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_calendar, container, false);
        mContext = container.getContext();
//        makeButton = view.findViewById(R.id.makeEventButton);
//        getButton = view.findViewById(R.id.getEventButton);
//
//        eventListView = view.findViewById(R.id.eventListView);
//        events = new ArrayList<>();
//        arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, events);
//
//        eventListView.setAdapter(arrayAdapter);
//        makeButton.setOnClickListener(this);
//        getButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.makeEventButton:
//                addEventOnCalendar();
//                break;
//            case R.id.getEventButton:
//                getEvent();
//                break;
//        }
    }

    public void getEvent() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Returning");
            return;
        }
        cursor = getActivity().getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor != null) {
                int id = cursor.getColumnIndex(CalendarContract.Events._ID);
                int titleId = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                int descripId = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
                int displayName = cursor.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME);

                String name = cursor.getString(displayName);
                String idValue = cursor.getString(id);
                String titleValue = cursor.getString(titleId);
                String descriptionValue = cursor.getString(descripId);

                Toast.makeText(getActivity(), name + ", " + idValue + ", " + titleValue + ", " + descriptionValue, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Event is not present", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addEventOnCalendar() {
        ContentResolver cr = getActivity().getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.TITLE, "Title of event");
        cv.put(CalendarContract.Events.DESCRIPTION, "Description of event");
        cv.put(CalendarContract.Events.DTSTART, Calendar.getInstance().getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, Calendar.getInstance().getTimeInMillis() + 60 * 15 * 1000);
        cv.put(CalendarContract.Events.CALENDAR_ID, 1);
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Permission denied");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, 42);
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);

        long eventId = Long.parseLong(uri.getLastPathSegment());
        System.out.println("eventId: " + eventId);
        String uriStr = "" + uri;
        String eventStr = "Title: " + ", Description: " + CalendarContract.Events.DESCRIPTION + ", START: "
                + CalendarContract.Events.DTSTART + ", END: " + CalendarContract.Events.DTEND;

        String eventStr2 = cv.getAsString(CalendarContract.Events.TITLE) + ", " + cv.getAsString(CalendarContract.Events.DESCRIPTION) + ", "
                + cv.getAsString(CalendarContract.Events.DTSTART) + ", " + cv.getAsString(CalendarContract.Events.DTEND);
        events.add(eventStr2);
        arrayAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "Event added", Toast.LENGTH_SHORT).show();
    }

}
