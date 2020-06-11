package com.example.balanceverattempt.LoggedInNav;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.balanceverattempt.util.FirebaseDatabaseHelper;
import com.example.balanceverattempt.adapters.EventAdapter;
import com.example.balanceverattempt.R;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Structure Day page after you Log in
 * Input wake up time, work time, expected work time, "blocks" times
 * "Add activities" and "Other" button
 * Press "confirm button"
 */

public class StructureDayFragment extends Fragment implements View.OnClickListener, CalendarView.OnDateChangeListener {

    private static final String TAG = "StructureDayFragment";

    private String currentDay;

    private static TextView selectDaytv;
    private EditText wakeUpEt, startWorkTimeEt, expectedWorkTimeEt;
    private TimePickerDialog wakeUpDialog, startWorkDialog, expectedWorkDialog;
    private Button calendarButton, confirmButton, addActivitiesButton, otherButton;

    private static ArrayList<String> customEventStartTimes = new ArrayList<>();
    private static ArrayList<String> customEventEndTimes = new ArrayList<>();
    private static HashMap<Integer, String> customEventNames;
    private static ListView customEventsListView;
    private static EventAdapter customEventsAdapter;
    private static ArrayList<Integer> customEventPositions = new ArrayList<>();
    private CalendarView mCalendarView;
    private MotionLayout motionLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.structure_your_day_motion_layout, container, false);

        motionLayout = view.findViewById(R.id.structure_your_day_motion_layout);

        customEventNames = new HashMap<>();
        customEventEndTimes.clear();
        customEventStartTimes.clear();
        customEventPositions.clear();
        System.out.println(TAG);

        selectDaytv = view.findViewById(R.id.selectTheDayTextView);
        wakeUpEt = view.findViewById(R.id.wakeUpEditText);
        startWorkTimeEt = view.findViewById(R.id.startWorkEditText);
        expectedWorkTimeEt = view.findViewById(R.id.expectedWorkTimeEditText);
        calendarButton = view.findViewById(R.id.calendarButton);
        confirmButton = view.findViewById(R.id.confirmDayButton);
        addActivitiesButton = view.findViewById(R.id.addActivitiesButton);
        otherButton = view.findViewById(R.id.otherButton);
        mCalendarView = view.findViewById(R.id.calendar_view);
        mCalendarView.setOnDateChangeListener(this);

        customEventsListView = view.findViewById(R.id.breakListView);

        wakeUpEt.setOnClickListener(this);
        startWorkTimeEt.setOnClickListener(this);
        expectedWorkTimeEt.setOnClickListener(this);
        calendarButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        addActivitiesButton.setOnClickListener(this);
        otherButton.setOnClickListener(this);

        customEventPositions.add(customEventPositions.size());

        customEventsAdapter = new EventAdapter(requireContext(), R.layout.listview_custom_breaks, customEventPositions);

        customEventsListView.setAdapter(customEventsAdapter);

        customEventsAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        customEventNames = new HashMap<>();
        customEventEndTimes.clear();
        customEventStartTimes.clear();
        customEventPositions.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmDayButton:
                confirmButtonOnClick();
                break;
            case R.id.calendarButton:
                calendarButtonOnClick();
                break;
            case R.id.wakeUpEditText:
                wakeUpOnClick();
                break;
            case R.id.startWorkEditText:
                startWorkOnClick();
                break;
            case R.id.expectedWorkTimeEditText:
                expectedWorkOnClick();
                break;
            case R.id.addActivitiesButton:
                addActivitiesOnClick();
                break;
            case R.id.otherButton:
                Toast.makeText(getActivity(), "Not configured yet.", Toast.LENGTH_SHORT);
                break;
            default:
                Log.d(TAG, "Default onClick triggered.");
                break;
        }
    }

    private void addActivitiesOnClick() {
        // Change to the Explore Activities Fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.logged_fragment_container, new ExploreActivitiesFragment(), "EXPLORE_ACITIVITIES_FRAGMENT").commit();
    }

    private void confirmButtonOnClick() {
        // Change to View Schedule Fragment to the certain day
        String currentDate;

        // Check if day selected
        if (CalendarFragment.getDateSelected() == null) {
            Toast.makeText(getActivity(), "Please Select a day", Toast.LENGTH_SHORT).show();
            calendarButton.requestFocus();
        } else if (wakeUpEt.getText().toString().isEmpty()) {
            // Check if wake up time is selected
            Toast.makeText(getActivity(), "Please select a time to wake up", Toast.LENGTH_SHORT).show();
            wakeUpEt.requestFocus();
        } else if (startWorkTimeEt.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please select a time to start work", Toast.LENGTH_SHORT).show();
            startWorkTimeEt.requestFocus();
        } else if (expectedWorkTimeEt.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please select an expected time to start work", Toast.LENGTH_SHORT).show();
            expectedWorkTimeEt.requestFocus();
        } else {
            currentDate = CalendarFragment.getDateSelected();
            // Converting String date selected (from Calendar) to Date object
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                Date currentDateAsDate = formatter.parse(currentDate);
                System.out.println("StructureDayFragment: Current date is : " + currentDateAsDate);
                addEvents(currentDateAsDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            openViewScheduleFragment();
        }
    }

    public void addEvents(Date date) {
        System.out.println("Adding events (in StructureDayFragment)");
        // Retrieving information from StructureDayFragment
        String wakeUpTime = wakeUpEt.getText().toString();
        String workTime = startWorkTimeEt.getText().toString();
        String expectedWorkTime = expectedWorkTimeEt.getText().toString();
        ArrayList<String> customEventStart = getCustomEventStartTimes();
        ArrayList<String> customEventEnd = getCustomEventEndTimes();

        // Getting Spinner name list
        ArrayList<String> spinnerNamesList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : customEventNames.entrySet()) {
            spinnerNamesList.add(entry.getValue());
        }

        List<Event> dayEvents = new ArrayList<>();

        Event wakeUpEvent = new Event(Color.argb(255, 169, 68, 65), date.getTime(), "Wake up time/" + wakeUpTime);
        Event workTimeEvent = new Event(Color.argb(255, 169, 68, 65), date.getTime(), "Work time/" + workTime);
        Event expectedWorkEvent = new Event(Color.argb(255, 169, 68, 65), date.getTime(), "Expected work time/" + expectedWorkTime);

        // Wake up EVENT/DATE
        dayEvents.add(wakeUpEvent);

        // Work time EVENT/DATE
        dayEvents.add(workTimeEvent);

        // Expected time EVENT/DATE
//        dayEvents.add(expectedWorkEvent);

        Iterator<String> it1 = customEventStart.iterator();
        Iterator<String> it2 = customEventEnd.iterator();
        Iterator<String> it3 = spinnerNamesList.iterator();

        while (it1.hasNext() && it2.hasNext() && it3.hasNext()) {
            Event customEvent = new Event(Color.argb(255, 169, 68, 65), date.getTime(), it3.next() + "/" + it1.next() + "/" + it2.next());
//            System.out.println("events: " + customEvent);
            dayEvents.add(customEvent);
        }

        // ADDING EVENTS TO USER
        // Current Outake: Adding the date may not be needed
        // Currently adding separate date and events.

        LoggedInActivity.getCurrentUser().addEventsForDay(dayEvents);

        // Updating user in database (due to the added events)
        updateUser(LoggedInActivity.getCurrentUser().getId());
    }

    private void updateUser(String id) {
        System.out.println("StructureDayFragment: updateUser: LoggedinActivity.getCurrentUser() \n" + LoggedInActivity.getCurrentUser());
        new FirebaseDatabaseHelper().updateUser(id, LoggedInActivity.getCurrentUser(), new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<HashMap<String, String>> users, List<String> keys) {
            }

            @Override
            public void DataIsInserted() {
            }

            @Override
            public void DataIsUpdated() {
                Toast.makeText(getActivity(), "Events added", Toast.LENGTH_SHORT).show();
                System.out.println("StructureDayFragment: updateUser: DataIsUpdated:\nEvents in current user: " + LoggedInActivity.getCurrentUser().getEventEventList());
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

    // Adds a position to positions list. Starts at 0. First row represents 0 position.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void addPosition() {
        System.out.println("StructureDayFragment: addPosition: AddPosition -> " + customEventPositions.size());
        customEventNames.put(customEventPositions.size(), "");
        customEventPositions.add(customEventPositions.size());
        customEventsAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(customEventsListView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void removePosition() {
        System.out.println("StructureDayFragment: removePosition: REMOVE position");
        if (customEventNames.containsKey(customEventPositions.size() - 1)) {
            System.out.println("StructureDayFragment: removePosition:\nbreakPositions.size-1 = " + customEventPositions.size());
            System.out.println("StructureDayFragment: removePosition: Removing spinnerItemMap(" + (customEventPositions.size() - 1) + "): " + customEventNames.get(customEventPositions.size() - 1));
            customEventNames.remove(customEventPositions.size() - 1);
        } else {
            System.out.println("StructureDayFragment: removePosition: Does not contain breakPositions.size() = " + customEventPositions.size());
        }
        customEventPositions.remove(customEventPositions.size() - 1);
        customEventsAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(customEventsListView);
    }

    public static ArrayList<Integer> getCustomEventPositions() {
        return customEventPositions;
    }

    private void calendarButtonOnClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.logged_fragment_container, new CalendarFragment(), "CALENDAR_FRAGMENT").commit();
    }

    private void expectedWorkOnClick() {
        expectedWorkDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                expectedWorkTimeEt.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, 0, 0, true);
        expectedWorkDialog.show();
    }

    private void startWorkOnClick() {
        startWorkDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startWorkTimeEt.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, 0, 0, true);
        startWorkDialog.show();
    }

    public void wakeUpOnClick() {
        wakeUpDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                wakeUpEt.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, 0, 0, true);
        wakeUpDialog.show();
    }

    public void openViewScheduleFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.logged_fragment_container, new ViewScheduleFragment(), "VIEW_SCHEDULE_FRAGMENT").commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        EventAdapter mAdapter = (EventAdapter) listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static ArrayList<String> getCustomEventStartTimes() {
        return customEventStartTimes;
    }

    public static void addCustomEventStartTime(String customEventStartTime) {
        System.out.println("StructureDayFragment: addStartBreak:\n\tbreakstarttime: " + customEventStartTime);
        if (customEventStartTime != null) {
            customEventStartTimes.add(customEventStartTime);
        }
    }

    public static ArrayList<String> getCustomEventEndTimes() {
        return customEventEndTimes;
    }

    public static void addCustomEventEndTime(String customEventEndTime) {
        if (customEventEndTime != null) {
            customEventEndTimes.add(customEventEndTime);
        }
    }

    public static void addCustomEventName(String name, int position) {
        if (name != null) {
            customEventNames.put(position, name);
        } else {
        }
        System.out.println(customEventNames);
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public static void calendarDaySelected(String date) {
//        String selectedDate = "Selected: " + date;
//        selectDaytv.setTextAppearance(R.style.date_selected_style);
//        selectDaytv.setText(selectedDate);
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        System.out.println("Day changed.");
        String dateSelected;
        if ((month + 1) < 10) {
            dateSelected = year + "/0" + (month + 1) + "/" + dayOfMonth;
        } else {
            dateSelected = year + "/" + (month + 1) + "/" + dayOfMonth;
        }
        Snackbar.make(view, "Date selected: " + dateSelected, Snackbar.LENGTH_LONG).show();
        if (dateSelected != null) {
            selectDaytv.setTextAppearance(R.style.date_selected_style);
            selectDaytv.setText(dateSelected);
        }
        motionLayout.transitionToStart();
    }
}
