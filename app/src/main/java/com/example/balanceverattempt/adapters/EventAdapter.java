package com.example.balanceverattempt.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.balanceverattempt.LoggedInNav.StructureDayFragment;
import com.example.balanceverattempt.R;
import com.example.balanceverattempt.models.ViewHolder;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventAdapter extends ArrayAdapter<Integer> implements AdapterView.OnItemSelectedListener {

    private static final String TAG = EventAdapter.class.getSimpleName();

    private ArrayList<Integer> positions;
    private int mPosition;
    private View parent;
    private Context mContext;
    private int mResource;
    private String currentStartTime, currentEndTime;
    private Button addButton;

    public EventAdapter(@NonNull Context context, int resource, ArrayList<Integer> positions) {
        super(context, resource, positions);
        mContext = context;
        mResource = resource;
        this.positions = positions;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        this.parent = (View) parent.getParent();
        View row;
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.listview_custom_breaks, parent, false);
            row.setTag(position);
            viewHolder = new ViewHolder();
            viewHolder.position = position;
            mPosition = viewHolder.position;
            //Spinner Implementation
            viewHolder.spinner = row.findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.spinnerItems, R.layout.spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            viewHolder.spinner.setAdapter(spinnerAdapter);
            viewHolder.spinner.setOnItemSelectedListener(this);
            viewHolder.spinner.setTag(Integer.valueOf(position));

            viewHolder.addButton = row.findViewById(R.id.addBreakButton);
            if (viewHolder.addButton.getVisibility() == View.GONE) {
            }
            viewHolder.startTimeEt = row.findViewById(R.id.startBreakEditText);
            viewHolder.toTimeEt = row.findViewById(R.id.toBreakEditText);

            // Setting start time for blocker (On Click)
            viewHolder.startTimeEt.setOnClickListener(v -> setStartBreakTime(viewHolder.startTimeEt));

            // Setting end time for blocker (On Click)
            viewHolder.toTimeEt.setOnClickListener(v -> setToBreakTime(viewHolder.toTimeEt, convertView));
            row.setTag(viewHolder);
        } else {
            row = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
            // To make sure if you're pressing the remove button, that the last displayed
            // row's button is visible.
            if (viewHolder.addButton.getVisibility() == View.INVISIBLE && viewHolder.position == (StructureDayFragment.getCustomEventPositions().size() - 1)) {
                viewHolder.addButton.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.position == 0 && viewHolder.addButton.getTag() == null) {
                    viewHolder.addButton.setBackgroundResource(R.drawable.ic_minus_button);
                    viewHolder.addButton.setTag("remove");
                    StructureDayFragment.addPosition();
                } else if (viewHolder.position == 0 && viewHolder.addButton.getTag().equals("remove") && StructureDayFragment.getCustomEventPositions().size() > 1) {
                    if (StructureDayFragment.getCustomEventPositions().size() == 2) {
                        viewHolder.addButton.setTag(null);
                        viewHolder.addButton.setBackgroundResource(R.drawable.ic_addition_button);
                    }
                    StructureDayFragment.removePosition();
                    // If you remove a position, you want to make the StructureDayFragment.getBreakPositions().size() position
                    // of viewHolder's add button visible
                } else if (viewHolder.position != 0) {
                    StructureDayFragment.addPosition();
                    viewHolder.addButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        viewHolder.position = position;
        mPosition = viewHolder.position;
        return row;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Nullable
    @Override
    public Integer getItem(int position) {
        return positions.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    // Setting start time for spinner
    private void setStartBreakTime(final EditText startTimeEt) {
        TimePickerDialog toTimeDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                currentStartTime = time;
                startTimeEt.setText(time);
                StructureDayFragment.addCustomEventStartTime(time);
            }
        }, 0, 0, true);
        toTimeDialog.show();
    }

    // Setting to time for spinner
    private void setToBreakTime(final EditText toTimeEt, View v) {
        TimePickerDialog startTimeDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                try {
                    if (checkIfAfter(currentStartTime, time)) {
                        toTimeEt.setText(time);
                        StructureDayFragment.addCustomEventEndTime(time);
                    } else {
                        Snackbar.make(parent, "Time selected must be AFTER start time.", Snackbar.LENGTH_SHORT).show();
                        toTimeEt.requestFocus();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, 0, 0, true);
        startTimeDialog.show();
    }

    // Returns TRUE if startTime is before endTime
    // Returns FALSE if startTime is after endTime
    private boolean checkIfAfter(String startTime, String endTime) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date startDate = dateFormat.parse(startTime);
        Date endDate = dateFormat.parse(endTime);

        if (startDate.after(endDate)) {
            return false;
        } else {
            return true;
        }
    }

    // Spinner Item selection logic
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String itemText = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), itemText + " selected", Toast.LENGTH_SHORT).show();
        StructureDayFragment.addCustomEventName(itemText, Integer.parseInt(parent.getTag().toString()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
