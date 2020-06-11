package com.example.balanceverattempt.models;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ViewHolder{
    public int position;
    public Spinner spinner;
    public EditText startTimeEt, toTimeEt;
    public Button addButton;

    @Override
    public String toString() {
        return "ViewHolder{" +
                "position=" + position +
                ", spinner=" + spinner +
                ", startTimeEt=" + startTimeEt +
                ", toTimeEt=" + toTimeEt +
                ", addButton=" + addButton +
                '}';
    }
}
