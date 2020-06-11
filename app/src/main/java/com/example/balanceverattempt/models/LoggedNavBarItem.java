package com.example.balanceverattempt.models;

import com.example.balanceverattempt.R;

public enum LoggedNavBarItem {
    STRUCTURE_YOUR_DAY(R.id.structureYourDaytv),
    VIEW_SCHEDULE(R.id.viewScheduletv),
    COPY_SCHEDULE(R.id.copyScheduletv),
    CHECK_YOUR_SCORE(R.id.checkYourScoretv),
    SHARE_YOUR_SCHEDULE(R.id.shareYourScheduletv),
    EXPLORE_ACTIVITIES(R.id.exploreActivitiestv),
    CONTACT(R.id.contacttv);

    private int itemId;
    LoggedNavBarItem(int itemId){
        this.itemId = itemId;
    }

    public int getItemId(){
        return itemId;
    }

    public static LoggedNavBarItem fromViewId(int viewId){
        for(LoggedNavBarItem navBarItem : LoggedNavBarItem.values()){
            if (navBarItem.getItemId() == viewId){
                return navBarItem;
            }
        }
        throw new Error("Cannot find viewType");
    }
}
