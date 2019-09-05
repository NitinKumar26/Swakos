package com.swakos.model;

public class Category {
    private int mIconResourceId;
    private String mName;

    public Category(int mIconResourceId, String name) {
        this.mIconResourceId = mIconResourceId;
        this.mName = name;
    }

    public int getmIconResourceId() {
        return mIconResourceId;
    }

    public void setmIconResourceId(int mIconResourceId) {
        this.mIconResourceId = mIconResourceId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
