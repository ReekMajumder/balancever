package com.example.balanceverattempt.LoggedInNav;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.balanceverattempt.R;
import com.google.android.material.snackbar.Snackbar;

// REDUNDANT -- Keeping because I don't delete code
public class CalendarFragment extends Fragment {

    private CalendarView mCalendarView;

    private static String dateSelected;
    private TextView selectDaytv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_layout, container, false);

        mCalendarView = view.findViewById(R.id.calendar_view);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                System.out.println("Day changed.");
                if ((month+1) < 10){
                    dateSelected = year+"/0"+(month+1)+"/"+dayOfMonth;
                } else {
                    dateSelected = year + "/" + (month + 1) + "/" + dayOfMonth;
                }
                Snackbar.make(view, "Date selected: " + dateSelected, Snackbar.LENGTH_LONG).show();
                if (dateSelected != null){
//                    StructureDayFragment.calendarDaySelected(dateSelected);
                    hideFragment();
                }
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void hideFragment(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .hide(this)
                .commit();
    }

    public static String getDateSelected() {
        return dateSelected;
    }

    public static void setDateSelected(String dateSelected) {
        CalendarFragment.dateSelected = dateSelected;
    }
}
