package com.example.balanceverattempt.LoggedNav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.balanceverattempt.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLContext;

public class CalendarFragment extends Fragment {

    private CalendarView mCalendarView;

    private static String dateSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_layout, container, false);

        mCalendarView = view.findViewById(R.id.calendar_view);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dateSelected = year+"/"+(month+1)+"/"+dayOfMonth;
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                Date date = new Date(dayOfMonth, month, year);
//                dateSelected = dateFormat.format(date);
                Toast.makeText(getActivity(), "Date selected:" + dateSelected, Toast.LENGTH_LONG).show();
                if (dateSelected != null){
                    hideFragment();
                }
            }
        });
        return view;
    }

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
