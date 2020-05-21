package com.example.balanceverattempt.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.balanceverattempt.LoggedNav.StructureDayFragment;
import com.example.balanceverattempt.R;
import com.example.balanceverattempt.models.ViewHolder;

import java.util.ArrayList;
import java.util.Locale;

public class ListAdapter extends ArrayAdapter<Integer> {

    private static final String TAG = ListAdapter.class.getSimpleName();

    private ArrayList<Integer> positions;
    private int mPosition;
    private Context mContext;
    private int mResource;
    private String adapterType;

    public ListAdapter(@NonNull Context context, int resource, ArrayList<Integer> positions) {
        super(context, resource, positions);
        mContext = context;
        mResource = resource;
        this.positions = positions;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.listview_custom_breaks, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.position = position;

            viewHolder.subTitle = row.findViewById(R.id.breakCustomTextView);
            viewHolder.addButton = row.findViewById(R.id.addBreakButton);
            viewHolder.startTimeEt = row.findViewById(R.id.startBreakEditText);
            viewHolder.toTimeEt = row.findViewById(R.id.toBreakEditText);

            viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.position == 0) {
                        if (viewHolder.addButton.getTag() == null) {
                            StructureDayFragment.addPosition();
                            viewHolder.addButton.setTag("remove");
                            viewHolder.addButton.setBackgroundResource(R.drawable.ic_remove_button);
                        } else {
                            // Remove Function
                            if (StructureDayFragment.getBreakPositions().size() != 1) {
                                StructureDayFragment.removePosition();
                            }
                            if (StructureDayFragment.getBreakPositions().size() == 1) {
                                viewHolder.addButton.setTag(null);
                                viewHolder.addButton.setBackgroundResource(R.drawable.ic_add_button);
                            }
                        }
                    } else {
                        viewHolder.addButton.setVisibility(View.GONE);
                        StructureDayFragment.addPosition();
                    }
                }
            });

            viewHolder.startTimeEt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setStartBreakTime(viewHolder.startTimeEt);
                }
            });

            viewHolder.toTimeEt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setToBreakTime(viewHolder.toTimeEt);
                }
            });
            row.setTag(viewHolder);
        } else {
            row = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.position = position;
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

    private void setStartBreakTime(final EditText startTimeEt) {
        TimePickerDialog toTimeDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                startTimeEt.setText(time);
                StructureDayFragment.addStartBreak(time);
            }
        }, 0, 0, true);
        toTimeDialog.show();
    }

    private void setToBreakTime(final EditText toTimeEt) {
        TimePickerDialog startTimeDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                toTimeEt.setText(time);
                StructureDayFragment.addToBreak(time);
            }
        }, 0, 0, true);
        startTimeDialog.show();
    }
}
