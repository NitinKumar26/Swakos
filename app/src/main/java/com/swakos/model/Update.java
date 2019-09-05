package com.swakos.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by belal on 19/4/17.
 */

public class Update {

    private int mImageResourceId;
    private String mName;

    public Update(String name, int mImageResourceId) {
        this.mName = name;
        this.mImageResourceId = mImageResourceId;
    }

    public int getmImageResourceId() {
        return mImageResourceId;
    }

    public void setmImageResourceId(int mImageResourceId) {
        this.mImageResourceId = mImageResourceId;
    }

}