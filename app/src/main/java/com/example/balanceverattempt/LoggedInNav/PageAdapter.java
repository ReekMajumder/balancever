package com.example.balanceverattempt.LoggedInNav;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.balanceverattempt.LoggedInNav.FreeTimeFragment;

public class PageAdapter extends FragmentPagerAdapter {
    private int noOfTabs;

    public PageAdapter(FragmentManager fm, int noOfTabs) {
        super(fm);
        this.noOfTabs=noOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new WorkFragment();
            case 1:
                return new FreeTimeFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
