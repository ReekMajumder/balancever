package com.example.balanceverattempt.LoggedNav;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.balanceverattempt.FirebaseDatabaseHelper;
import com.example.balanceverattempt.R;
import com.example.balanceverattempt.models.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.provider.CalendarContract.*;

public class ViewScheduleFragment extends Fragment {
    Context mContext;

    String currentDate;

    private String currentId;
    TextView currentDateTextView;
    ListView eventsList;
    CompactCalendarView compactCalendarView;
    ArrayAdapter adapter;
    List<String> dayEvents;

    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    public static final String[] EVENT_PROJECTION = new String[]{
            Calendars._ID,
            Calendars.ACCOUNT_NAME,
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.OWNER_ACCOUNT
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_schedule, container, false);
        mContext = container.getContext();


        compactCalendarView = view.findViewById(R.id.compactcalendar_view);

        currentDateTextView = view.findViewById(R.id.currentDayTextView);
        eventsList = view.findViewById(R.id.currentDayEventsListView);

        dayEvents = new ArrayList<>();
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, dayEvents);
        eventsList.setAdapter(adapter);

        compactCalendarView.showCalendarWithAnimation();
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        currentDateTextView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentDate = bundle.getString("current_date");

            System.out.println("Current Date in string: " + currentDate);

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                Date currentDateAsDate = formatter.parse(currentDate);
                System.out.println("Current date is : " + currentDateAsDate);
                addEvents(currentDateAsDate, bundle);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // Clear events for a new day
                dayEvents.clear();
                // logging events
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Log.d(String.valueOf(this), "Day was clicked: " + dateClicked + " with events " + events);

                // Printing events to list view
                for (Event e : events) {
                    dayEvents.add(e.getData().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currentDateTextView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

                Log.d(String.valueOf(this), "Month was scrolled to: " + firstDayOfNewMonth);
            }
        });
        //getEventsFromDatabase(LoggedInActivity.getCurrentUser().getId());
        return view;
    }

    public void addEvents(Date date, Bundle bundle) {
        System.out.println("Adding events");
        // Retrieving information from StructureDayFragment
        String wakeUpTime = bundle.getString("wake_up_time");
        String workTime = bundle.getString("start_work_time");
        String expectedWorkTime = bundle.getString("expected_work_time");
        ArrayList<String> breaksStart = bundle.getStringArrayList("breaks_start_list");
        ArrayList<String> breaksEnd = bundle.getStringArrayList("breaks_to_list");

        List<Event> dayEvents = new ArrayList<>();

        Event wakeUpEvent = new Event(Color.argb(255, 169, 68, 65), date.getTime(), "Wake up time: " + wakeUpTime);
        Event workTimeEvent = new Event(Color.argb(255, 169, 68, 65), date.getTime(), "Work time: " + workTime);
        Event expectedWorkEvent = new Event(Color.argb(255, 169, 68, 65), date.getTime(), "Expected work time: " + expectedWorkTime);
        dayEvents.add(wakeUpEvent);
        dayEvents.add(workTimeEvent);
        dayEvents.add(expectedWorkEvent);
        compactCalendarView.addEvent(wakeUpEvent);
        compactCalendarView.addEvent(workTimeEvent);
        compactCalendarView.addEvent(expectedWorkEvent);

        Iterator<String> it1 = breaksStart.iterator();
        Iterator<String> it2 = breaksEnd.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            Event breakEvent = new Event(Color.argb(255, 169, 68, 65), date.getTime(), "Break: " + it1.next() + " to " + it2.next());
            compactCalendarView.addEvent(breakEvent);
            dayEvents.add(breakEvent);
//            eventData = eventData + "\n\t\t"+it1.next()+" to "+it2.next();
        }

        System.out.println(LoggedInActivity.getCurrentUser().getName());
//        getUserId(LoggedInActivity.getCurrentUser().getName());
        LoggedInActivity.getCurrentUser().addEventsForDay(date, dayEvents);
        updateUser(LoggedInActivity.getCurrentUser().getId());
    }

    private void updateUser(String id) {
        String key = id;
        System.out.println(id);
        new FirebaseDatabaseHelper().updateUser(key, LoggedInActivity.getCurrentUser(), new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<User> users, List<String> keys) {
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                Toast.makeText(getActivity(), "Events added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void DataIsDeleted() {

            }

            @Override
            public void DataEventIsLoaded(List<Event> eventList) {

            }
        });
    }

    public void getEventsFromDatabase(final String id) {
        // ADD ALL EVENTS IN THE SPECIFIC USER INTO THE CALENDAR
        new FirebaseDatabaseHelper().readEventsFromUser(id, LoggedInActivity.getCurrentUser(), new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<User> users, List<String> keys) {
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
                for (Event e : eventList) {
                    addEventToCalendar(e);
                }
            }
        });
    }

    public void addEventToCalendar(Event event) {
        compactCalendarView.addEvent(event);
    }

    // Convert Date into MilliSeconds, to add to correct Day.
    public long datetoMillis(String day) {
        day = day.replace("AM", "");
        day = day.replace("PM", "");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        return millis;
    }
//    public void getEvent(Date date) {
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            System.out.println("Returning");
//            return;
//        }
//        Cursor cursor = null;
//        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
//
////        ContentUris.appendId(eventsUriBuilder, Date);
//        cursor = getActivity().getContentResolver().query(Events.CONTENT_URI, null, null, null, null);
//        while (cursor.moveToNext()) {
//            if (cursor != null) {
//                int id = cursor.getColumnIndex(Events._ID);
//                int titleId = cursor.getColumnIndex(Events.TITLE);
//                int descripId = cursor.getColumnIndex(Events.DESCRIPTION);
//                int displayName = cursor.getColumnIndex(Events.CALENDAR_DISPLAY_NAME);
//
//                String name = cursor.getString(displayName);
//                String idValue = cursor.getString(id);
//                String titleValue = cursor.getString(titleId);
//                String descriptionValue = cursor.getString(descripId);
//
//
//                Toast.makeText(getActivity(), name + ", " + idValue + ", " + titleValue + ", " + descriptionValue, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getActivity(), "Event is not present", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}
