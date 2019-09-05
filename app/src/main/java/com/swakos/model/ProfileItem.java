package com.swakos.model;

public class ProfileItem {
    private String mItemName;
    private int mIconResourceId;

    public ProfileItem(String mItemName, int mIconResourceId) {
        this.mItemName = mItemName;
        this.mIconResourceId = mIconResourceId;
    }

    public String getmItemName() {
        return mItemName;
    }

    public void setmItemName(String mItemName) {
        this.mItemName = mItemName;
    }

    public int getmIconResourceId() {
        return mIconResourceId;
    }

    public void setmIconResourceId(int mIconResourceId) {
        this.mIconResourceId = mIconResourceId;
    }
}
