package com.example.balanceverattempt.LoggedInNav;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.balanceverattempt.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class CheckYourScoreFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    TabItem WorTabItem;
    TabItem FreeTabItem;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       // return inflater.inflate(R.layout.fragment_check_your_score, container, false);
        View view=inflater.inflate(R.layout.fragment_check_your_score,container,false);
        //This is Pratik Code Branch
        tabLayout= view.findViewById(R.id.tabLayout);
        WorTabItem = view.findViewById(R.id.workTab);
        FreeTabItem = view.findViewById(R.id.freeTimeTab);
        viewPager = view.findViewById(R.id.viewpager);

        pageAdapter=new PageAdapter(getFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
   //     viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        return view;
    }

}
