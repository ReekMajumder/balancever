package com.example.balanceverattempt.LoggedInNav;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.example.balanceverattempt.util.FirebaseDatabaseHelper;
import com.example.balanceverattempt.R;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewScheduleFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ViewScheduleFragment";
    Context mContext;

    RelativeLayout mRelativeLayout;
    Button prevDayBtn, nextDayBtn;
    TextView currentDateTextView, dayViewCurrentDateTextView;
    CompactCalendarView compactCalendarView;
    ObservableScrollView observableScrollView;
    Date currentDate;

    List<String> dayEvents;
    private int createdEventsCount;

    /*
        HashMap
            -> Key = index of event in dayEvents
            -> Value = list of integers (integers represent indexes of events it overlaps
    */
    private HashMap<Integer, ArrayList<Integer>> overlappingCountMap;
    private HashMap<Integer, List<Integer>> overlappingListMap;

    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatDayView = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_schedule_motion_layout, container, false);

        mContext = container.getContext();
        createdEventsCount = 0;

        mRelativeLayout = view.findViewById(R.id.left_event_column);
        prevDayBtn = view.findViewById(R.id.prevDayButton);
        nextDayBtn = view.findViewById(R.id.nextDayButton);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        currentDateTextView = view.findViewById(R.id.currentDayTextView);
        dayViewCurrentDateTextView = view.findViewById(R.id.display_current_date);

        prevDayBtn.setOnClickListener(this);
        nextDayBtn.setOnClickListener(this);

        currentDate = new Date();
        dayViewCurrentDateTextView.setText(dateFormatDayView.format(currentDate));
        dayEvents = new ArrayList<>();

//        compactCalendarView.showCalendar();
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setShouldDrawDaysHeader(true);
        compactCalendarView.setCurrentDayBackgroundColor(Color.parseColor("#52307C"));
        compactCalendarView.setCalendarBackgroundColor(Color.parseColor("#69B2D2"));
        compactCalendarView.setCurrentSelectedDayBackgroundColor(Color.parseColor("#B491C8"));

        currentDateTextView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        // Adding all events from user (in database) to calendar
        getEventsFromDatabase(LoggedInActivity.getCurrentUser().getId());

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDayClick(Date dateClicked) {
                onDayChange(dateClicked);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currentDateTextView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
                Log.d(String.valueOf(this), "Month was scrolled to: " + firstDayOfNewMonth);
                onDayChange(firstDayOfNewMonth);
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDayChange(Date dateClicked) {
        currentDate = dateClicked;
        // Text View for month change
        currentDateTextView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        // Day View text change
        dayViewCurrentDateTextView.setText(dateFormatDayView.format(currentDate));
        // Clear events for a new day
        clearingEventView();
        dayEvents.clear();

        // logging events
        List<Event> events = compactCalendarView.getEvents(dateClicked);
        Log.d(String.valueOf(this), "Day was clicked: " + dateClicked + " with events " + events);

        // Printing events to list view
        for (Event e : events) {
            dayEvents.add(e.getData().toString());
        }

        // Displaying events to day view
        try {
            displayDailyEvents(dayEvents);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevDayButton:
                prevDayOnClick();
                break;
            case R.id.nextDayButton:
                nextDayOnClick();
                break;
        }
    }

    // Handling button onClick for previous day in header of dayview
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void prevDayOnClick() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -1);
        currentDate = c.getTime();

        // Setting current date in compact calendar view
        compactCalendarView.setCurrentDate(currentDate);
        onDayChange(currentDate);
        // Changing textview headers

    }

    // Handling button onClick for next day in header of dayview
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextDayOnClick() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, 1);
        currentDate = c.getTime();

        // Setting current date in compact calendar view
        compactCalendarView.setCurrentDate(currentDate);
        onDayChange(currentDate);
    }

    // Getting the events for the day and displaying them
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayDailyEvents(List<String> dayEvents) throws ParseException {
        overlappingCountMap = new HashMap<>();
        overlappingListMap = new HashMap<>();
        // Event info: [n][0] has block height, [n][1] has block width
        int[][] eventInfo = new int[dayEvents.size()][2];
        // Date info: [n][0] has start time, [n][1] has end time
        Date[][] dateEventInfo = new Date[dayEvents.size()][2];

        // Instantiating eventInfo
        for (String eventData : dayEvents) {
            // Parsing eventData
            String[] splitData = eventData.split("/");
            // Start time & End time are in the format of : HH:mm
            String startTime = splitData[1];
            // Calculate end time
            String endTime = getEndTime(splitData, startTime);
            // Formatting start and end time into Date Object
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date startDate = simpleDateFormat.parse(startTime);
            Date endDate = simpleDateFormat.parse(endTime);

            dateEventInfo[dayEvents.indexOf(eventData)][0] = startDate;
            dateEventInfo[dayEvents.indexOf(eventData)][1] = endDate;

            int eventBlockHeight = getEventTimeFrame(startTime, endTime);
            eventInfo[dayEvents.indexOf(eventData)][0] = eventBlockHeight;
            Log.d(TAG, "Height " + eventBlockHeight);
        }

        // Displaying event
        // Placed in another for loop because dateEventInfo has to be instantiated entirely before
        // this loop runs
        for (String eventData : dayEvents) {
            String[] splitData = eventData.split("/");
            // Start time & End time are in the format of : HH:mm
            String startTime = splitData[1];
            // Calculate end time
            String endTime = getEndTime(splitData, startTime);
            // Title of the event
            String title = splitData[0];
            String description = "(" + splitData[1] + " to " + endTime + ")";

            // Formatting start and end time into Date Object
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date startDate = simpleDateFormat.parse(startTime);
            List<Integer> eventStack = getEventStack(dayEvents.indexOf(eventData), dateEventInfo);
            // Finding the max stack size with the current index in the stack
            int width = mRelativeLayout.getWidth() / eventStack.size();
            int leftMargin = (eventStack.size() - 1);

            // FIND INDEX OF current event in EVENTSTACK and multiplies it by width
            for (Integer eventIndex : eventStack) {
                if (eventIndex == dayEvents.indexOf(eventData)) {
                    leftMargin = eventStack.indexOf(eventIndex);
                }
            }
            displayEventSection(leftMargin, startDate, eventInfo[dayEvents.indexOf(eventData)][0], width, title, description);
        }
    }

    // If end time does not exist, the endTime is 1 hour after startTime
    private String getEndTime(String[] splitData, String startTime) {
        if (splitData.length == 2) {
            String[] hoursMinutes = startTime.split(":");
            int hours = Integer.parseInt(hoursMinutes[0]);
            hours++;
            hoursMinutes[0] = "" + hours;
            return hoursMinutes[0] + ":" + hoursMinutes[1];
        } else {
            return splitData[2];
        }
    }

    // Gets the pixels at which the event starts in the Day View
    // String startTime and endTime are both in time format "HH:mm"
    private int getEventTimeFrame(String startTime, String endTime) {
        // Calculate time difference
        // Note: startTimeSplit[0] and endTimeSplit[0] represent hours
        //       startTimeSplit[1] and endTimeSplit[1] represent minutes
        String[] startTimeSplit = startTime.split(":");
        String[] endTimeSplit = endTime.split(":");

        // hour difference
        int hours = Integer.parseInt(endTimeSplit[0]) - Integer.parseInt(startTimeSplit[0]);

        // minutes difference
        int minutes = Integer.parseInt(endTimeSplit[1]) - Integer.parseInt(startTimeSplit[1]);
        return (hours * 60) + ((minutes * 60) / 100);
    }

    private List<Integer> getEventStack(int indexOfEvent, Date[][] dateEventInfo) {
        // Initializing stack
        overlappingListMap.put(indexOfEvent, new ArrayList<>());
        overlappingListMap.get(indexOfEvent).add(indexOfEvent);
        List<Integer> nextEventList = checkIfEventOverlapsNextEvent(dateEventInfo, indexOfEvent, indexOfEvent + 1, overlappingListMap.get(indexOfEvent));
        return checkIfEventOverlapsPreviousEvent(dateEventInfo, indexOfEvent, indexOfEvent - 1, nextEventList);
    }

    // Checks for any overlapping events that occur after the current event
    private List<Integer> checkIfEventOverlapsNextEvent(Date[][] dateEventInfo, int indexOfEvent, int indexOfNextEvent, List<Integer> overlappingEventList) {
        if (indexOfNextEvent >= dayEvents.size()) {
            return overlappingEventList;
        } else if (datesOverlap(dateEventInfo[indexOfEvent][0], dateEventInfo[indexOfEvent][1], dateEventInfo[indexOfNextEvent][0], dateEventInfo[indexOfNextEvent][1])) {
            overlappingEventList.add(indexOfNextEvent);
            return checkIfEventOverlapsNextEvent(dateEventInfo, indexOfNextEvent, indexOfNextEvent + 1, overlappingEventList);
        } else {
            return overlappingEventList;
        }
    }

    // Checks for any overlapping events that occur before the current event
    private List<Integer> checkIfEventOverlapsPreviousEvent(Date[][] dateEventInfo, int indexOfEvent, int indexOfPrevEvent, List<Integer> overlappingEventList) {
        if (indexOfPrevEvent < 0) {
            return overlappingEventList;
        } else if (datesOverlap(dateEventInfo[indexOfEvent][0], dateEventInfo[indexOfEvent][1], dateEventInfo[indexOfPrevEvent][0], dateEventInfo[indexOfPrevEvent][1])) {
            overlappingEventList.add(0, indexOfPrevEvent);
            return checkIfEventOverlapsPreviousEvent(dateEventInfo, indexOfPrevEvent, indexOfPrevEvent - 1, overlappingEventList);
        } else {
            return overlappingEventList;
        }
    }

    private boolean datesOverlap(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        return startDate1.before(endDate2) && startDate2.before(endDate1);
    }

    // Displays event
    // startDate -> used to extract start time and hours
    // height -> calculated from getEventTimeFrame
    // title -> title of event
    // description -> description of event
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayEventSection(int leftMargin, Date startDate, int height, int width, String title, String description) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String displayValue = timeFormatter.format(startDate);
        String[] hoursMinutes = displayValue.split(":");
        int hours = Integer.parseInt(hoursMinutes[0]);
        int minutes = Integer.parseInt(hoursMinutes[1]);
        Log.d(TAG, "displayEventSection: " + title + " Hour value: " + hours);
        Log.d(TAG, "displayEventSection: " + title + " Minutes values: " + minutes);
        int topViewMargin = (hours * 60) + ((minutes * 60) / 100);
        Log.d(TAG, "displayEventSection: Top Margin: " + topViewMargin);
        createEventView(leftMargin, topViewMargin, height, width, title, description);
    }

    // Creates textview and adds it to mRelativeLayout
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createEventView(int leftMargin, int topMargin, int height, int width, String title, String description) {
        TextView mEventView = new TextView(getContext());
        RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // Ratio to adjust between screen sizes
        float dpRatio = getContext().getResources().getDisplayMetrics().density;
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        leftMargin = leftMargin * width;
        lParam.setMargins((int) (leftMargin), (int) (topMargin * dpRatio), 0, 0);
        mEventView.setLayoutParams(lParam);
        mEventView.setPadding((int) (15 * dpRatio), 0, (int) (15 * dpRatio), 0);
        mEventView.setHeight((int) (height * dpRatio));
        mEventView.setWidth((int) (width));
        mEventView.setGravity(0x11);
        mEventView.setElevation(20);
        mEventView.setAutoSizeTextTypeUniformWithConfiguration(5, 18, 1, TypedValue.COMPLEX_UNIT_SP);
        mEventView.setTextColor(Color.parseColor("#ffffff"));
        mEventView.setText(title + "\n" + description);
        Drawable eventBackground = AppCompatResources.getDrawable(getContext(), R.drawable.calendar_event_background);
        if (title.equals("Wake up time")) {
            eventBackground.setTint(Color.parseColor("#FF614D"));
//            mEventView.setBackgroundColor(Color.parseColor("#FF614D"));
        } else if (title.equals("Work time")) {
            eventBackground.setTint(Color.parseColor("#69B2D2"));
//            mEventView.setBackgroundColor(Color.parseColor("#69B2D2")); // done
        } else if (title.equals("Break")) {
            eventBackground.setTint(Color.parseColor("#A49AFF"));
//            mEventView.setBackgroundColor(Color.parseColor("#A49AFF")); // done
        } else if (title.equals("Sleep Time")) {
            eventBackground.setTint(Color.parseColor("#BEBEBE"));
//            mEventView.setBackgroundColor(Color.parseColor("#BEBEBE"));
        } else {
            eventBackground.setTint(Color.parseColor("#FF614D"));
//            mEventView.setBackgroundColor(Color.parseColor("#FF614D")); // done
        }
        mEventView.setBackground(eventBackground);
        mRelativeLayout.addView(mEventView, mRelativeLayout.getChildCount());
        createdEventsCount++;
    }

    private void clearingEventView() {
        for (int i = createdEventsCount; i > 0; i--) {
            System.out.println(mRelativeLayout.getChildCount());
            mRelativeLayout.removeViewAt(mRelativeLayout.getChildCount() - 1);
        }
        createdEventsCount = 0;
    }

    public void getEventsFromDatabase(final String id) {
        // ADD ALL EVENTS IN THE SPECIFIC USER INTO THE CALENDAR
        new FirebaseDatabaseHelper().readEventsFromUser(id, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<HashMap<String, String>> users, List<String> keys) {
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

            @Override
            public void GoogleUserExists(boolean value) {

            }
        });
    }

    public void addEventToCalendar(Event event) {
        compactCalendarView.addEvent(event);
    }
}
