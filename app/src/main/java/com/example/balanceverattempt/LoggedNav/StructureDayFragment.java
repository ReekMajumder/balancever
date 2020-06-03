package com.example.balanceverattempt.LoggedNav;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.balanceverattempt.adapters.ListAdapter;
import com.example.balanceverattempt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure Day page after you Log in
 * Input wake up time, work time, expected work time, "blocks" times
 * "Add activities" and "Other" button
 * Press "confirm button"
 */

public class StructureDayFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "StructureDayFragment";

    private String currentDay;

    private EditText wakeUpEt, startWorkTimeEt, expectedWorkTimeEt;
    private TimePickerDialog wakeUpDialog, startWorkDialog, expectedWorkDialog;
    private Button calendarButton, confirmButton, addActivitiesButton, otherButton;

    private static ArrayList<String> startBreaksList = new ArrayList<>();
    private static ArrayList<String> toBreaksList = new ArrayList<>();
    private static ListView breakListView;
    private static ListAdapter breakListAdapter;
    private static ArrayList<Integer> breakPositions = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_structure_your_day, container, false);

        System.out.println(TAG);

        wakeUpEt = view.findViewById(R.id.wakeUpEditText);
        startWorkTimeEt = view.findViewById(R.id.startWorkEditText);
        expectedWorkTimeEt = view.findViewById(R.id.expectedWorkTimeEditText);
        calendarButton = view.findViewById(R.id.calendarButton);
        confirmButton = view.findViewById(R.id.confirmDayButton);
        addActivitiesButton = view.findViewById(R.id.addActivitiesButton);
        otherButton = view.findViewById(R.id.otherButton);

        breakListView = view.findViewById(R.id.breakListView);

        wakeUpEt.setOnClickListener(this);
        startWorkTimeEt.setOnClickListener(this);
        expectedWorkTimeEt.setOnClickListener(this);
        calendarButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        addActivitiesButton.setOnClickListener(this);
        otherButton.setOnClickListener(this);

        breakPositions.add(breakPositions.size());

        breakListAdapter = new ListAdapter(requireContext(), R.layout.listview_custom_breaks, breakPositions);

        breakListView.setAdapter(breakListAdapter);

        breakListAdapter.notifyDataSetChanged();
        return view;
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

    private void addActivitiesOnClick(){
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
            Bundle bundleToSend = new Bundle();

            // Day selected
            bundleToSend.putString("current_date", currentDate);

            // Wake up time
            bundleToSend.putString("wake_up_time", wakeUpEt.getText().toString());

            // Work time
            bundleToSend.putString("start_work_time", startWorkTimeEt.getText().toString());

            // Expected work time
            bundleToSend.putString("expected_work_time", expectedWorkTimeEt.getText().toString());

            // Breaks
            bundleToSend.putStringArrayList("breaks_start_list", getStartBreaksList());
            bundleToSend.putStringArrayList("breaks_to_list", getToBreaksList());
            openViewScheduleFragment(bundleToSend);
        }

    }

    public static void addPosition() {
        System.out.println("AddPosition -> If BREAK");
        breakPositions.add(breakPositions.size());
        breakListAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(breakListView);
    }

    public static void removePosition() {
        System.out.println("REMOVE position");
        breakPositions.remove(breakPositions.size() - 1);
        breakListAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(breakListView);
    }

    public static ArrayList<Integer> getBreakPositions() {
        return breakPositions;
    }

    private void calendarButtonOnClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
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


    public void openViewScheduleFragment(Bundle bundle) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ViewScheduleFragment viewScheduleFragment = new ViewScheduleFragment();
        viewScheduleFragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.logged_fragment_container, viewScheduleFragment, "VIEW_SCHEDULE_FRAGMENT").commit();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter mAdapter = (ListAdapter) listView.getAdapter();
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

    public boolean checkIfValidSubmission() {
        return true;
    }

    public static ArrayList<String> getStartBreaksList() {
        return startBreaksList;
    }

    public static void setStartBreaksList(List<String> startBreaksList) {
        StructureDayFragment.startBreaksList = (ArrayList<String>) startBreaksList;
    }

    public static void addStartBreak(String breakStartTime) {
        System.out.println("breakstarttime: " + breakStartTime);
        if (breakStartTime != null) {
            startBreaksList.add(breakStartTime);
        }
    }

    public static void removeStartBreak(String breakStartTime) {
        startBreaksList.remove(breakStartTime);
    }

    public static ArrayList<String> getToBreaksList() {
        return toBreaksList;
    }

    public static void setToBreaksList(List<String> toBreaksList) {
        StructureDayFragment.toBreaksList = (ArrayList<String>) toBreaksList;
    }

    public static void addToBreak(String breakToTime) {
        if (breakToTime != null) {
            toBreaksList.add(breakToTime);
        }
    }

    public static void removeToBreak(String breakToTime) {
        toBreaksList.remove(breakToTime);
    }
}
