package com.example.balanceverattempt.LoggedInNav;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.balanceverattempt.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkFragment extends Fragment {
    private LineChart mChart;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;
    TextView displayPercent;
  //  LineChart lineChart;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkFragment newInstance(String param1, String param2) {
        WorkFragment fragment = new WorkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_work, container, false);
        ProgressBar progressBar= view.getRootView().findViewById(R.id.progressBar1);
        //  int progressValue= progressBar.getProgress();
          displayPercent= view.findViewById(R.id.showprogress);

          //lineChart=(LineChart) view.findViewById(R.id.linechart);
        int progress=70;
        displayPercent.setText(""+progress+"%");
        WaveLoadingView waveLoadingView= view.findViewById(R.id.waveloading);
        waveLoadingView.setProgressValue(progress);
        mChart= (LineChart) view.findViewById(R.id.chartwork);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxis= mChart.getAxisRight();
        xAxis.setDrawGridLinesBehindData(false);
        //mChart.getAxisLeft();
        mChart.getAxisLeft().setDrawGridLines(false);
        yAxis.setEnabled(false);
//        yAxis.setAxisMaxValue(9);
//        yAxis.setAxisMinValue(0);
        mChart.getAxisLeft().setAxisMinimum(0);
        mChart.getAxisLeft().setAxisMaximum(10);
        yAxis.setLabelCount(7);
        yAxis.setDrawGridLines(false);
        mChart.getDescription().setEnabled(false);
//        LineDataSet set1= new LineDataSet(dataValues1(),"Free time");
//        LineDataSet set2= new LineDataSet(dataValues2(),"Work time");
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//        dataSets.add(set2);
        LineDataSet set1, set2;
        set1= new LineDataSet(dataValues1(),"Free time");
        set2= new LineDataSet(dataValues2(),"Work time");
       // LineData data = new LineData(dataSets);
        LineData data = new LineData(set2,set1);
        data.setDrawValues(false);
        set1.setDrawCircles(false);
        set2.setDrawCircles(false);
       // set1.setColor(R.color.time_color);
        set1.setDrawFilled(true);
        Drawable drawable= ContextCompat.getDrawable(getContext(),R.drawable.line_chart_freetime);
        set1.setLineWidth(3f);
        set1.setFillDrawable(drawable);
        set2.setDrawFilled(true);
        Drawable drawable1=ContextCompat.getDrawable(getContext(),R.drawable.line_chart_worktime);
        set2.setFillDrawable(drawable1);

        //set1.setFillColor(R.color.time_color);
        //  Drawable drawable= ContextCompat.getDrawable(this.getContext(),R.drawable.chart_fill);
        //  Drawable drawable = ContextCompat.getDrawable(context,R.drawable.chart_fill);
//        Drawable drawable= getActivity().getResources().getDrawable(R.drawable.chart_fill);
//        set1.setFillDrawable(drawable);
        mChart.setData(data);
        mChart.invalidate();
//        mChart.setGridBackgroundColor(255);
        //  setData(40,60);
        //  mChart.animateX(1000);
        //  waveLoadingView=view.findViewById(R.id.waveloading);
        //waveLoadingView.setProgressValue(progressValue);
        return view;


    }


    private ArrayList<Entry> dataValues1(){
        ArrayList<Entry> dataVals= new ArrayList<Entry>();
        dataVals.add(new Entry(0,7));
        dataVals.add(new Entry(5, (float) 7.4));
        dataVals.add(new Entry(10, (float) 6.9));
        dataVals.add(new Entry(15, (float) 7.5));
        dataVals.add(new Entry(20, (float) 7.5));
        dataVals.add(new Entry(25, (float) 7.9));
        dataVals.add(new Entry(30, (float) 7.9));
        return dataVals;
    }

    private ArrayList<Entry> dataValues2(){
        ArrayList<Entry> dataVals= new ArrayList<Entry>();
        dataVals.add(new Entry(0,8));
        dataVals.add(new Entry(5, (float) 8.2));
        dataVals.add(new Entry(10, (float) 8.1));
        dataVals.add(new Entry(15, (float) 8.5));
        dataVals.add(new Entry(20, 9));
        dataVals.add(new Entry(25, (float) 8.5));
        dataVals.add(new Entry(30, (float) 8.6));
        return dataVals;
    }

//    private void setData(int count, int range){
//
//        XAxis xAxis=mChart.getXAxis();
//        xAxis.disableGridDashedLine();
//        xAxis.setDrawGridLinesBehindData(false);
//        ArrayList<Entry> yVals1= new ArrayList<>();
//        for (int i=0;i<count;i++)
//        {
//            float val=(float) (Math.random()*range)+9;
//            yVals1.add(new Entry(i,val));
//        }
//
//        ArrayList<Entry> yVals2 = new ArrayList<>();
//        for (int i=0;i<count;i++)
//        {
//            float val=(float) (Math.random()*range);
//            yVals2.add(new Entry(i,val));
//        }
//
////        ArrayList<Entry> yVals3= new ArrayList<>();
////        for (int i=0;i<count;i++)
////        {
////            float val=(float) (Math.random()*range)+50;
////            yVals3.add(new Entry(i,val));
////        }
//        LineDataSet set1, set2, set3;
//        set1= new LineDataSet(yVals1,"Work Time");
//
//        set2 = new LineDataSet(yVals2,"Free Time");
//
//       // set3 = new LineDataSet(yVals3, "Data Set3");
//        LineData data= new LineData(set1,set2);
//        mChart.setData(data);
////        mChart.setDrawGridBackground(false);
//    }
    }
