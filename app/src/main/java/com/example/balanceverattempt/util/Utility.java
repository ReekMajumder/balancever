package com.example.balanceverattempt.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {

    public static void setListViewHeightBasedOnChildren(ListView lv){
        ListAdapter listAdapter = (ListAdapter) lv.getAdapter();
        if (listAdapter == null){
            return;
        }

        int totalHeight = 100;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(lv.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++){
            View listItem = listAdapter.getView(i, null, lv);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight()*(listAdapter.getCount())-1);
        lv.setLayoutParams(params);
        lv.requestLayout();
    }

}
