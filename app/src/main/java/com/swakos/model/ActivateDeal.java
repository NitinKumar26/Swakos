package com.swakos.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivateDeal implements Parcelable {
    private String deal_id, deal_title, deal_doc_id, status, status_client_doc_id,activeDealDocId;
    private int deal_actual_price, deal_discounted_price;
    private int currentDate;
    private Timestamp created_at;
    private Map<String, Object> client_details;

    public ActivateDeal(){}


    public String getActiveDealDocId() {
        return activeDealDocId;
    }

    public void setActiveDealDocId(String activeDealDocId) {
        this.activeDealDocId = activeDealDocId;
    }

    public String getStatus_client_doc_id() {
        return status_client_doc_id;
    }

    public void setStatus_client_doc_id(String status_client_doc_id) {
        this.status_client_doc_id = status_client_doc_id;
    }

    protected ActivateDeal(Parcel parcel){
        deal_id = parcel.readString();
        deal_title = parcel.readString();
        deal_doc_id = parcel.readString();
        status = parcel.readString();
        activeDealDocId = parcel.readString();
        status_client_doc_id = parcel.readString();
        deal_actual_price = parcel.readInt();
        deal_discounted_price = parcel.readInt();
        currentDate = parcel.readInt();
        client_details = new HashMap<>();
        parcel.readMap(client_details, Object.class.getClassLoader());

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(deal_id);
        parcel.writeString(deal_title);
        parcel.writeString(deal_doc_id);
        parcel.writeString(status);
        parcel.writeString(activeDealDocId);
        parcel.writeString(status_client_doc_id);
        parcel.writeInt(deal_actual_price);
        parcel.writeInt(deal_discounted_price);
        parcel.writeInt(currentDate);
        parcel.writeMap(client_details);
    }

    public static final Creator<ActivateDeal> CREATOR = new Creator<ActivateDeal>() {
        @Override
        public ActivateDeal createFromParcel(Parcel parcel) {
            return new ActivateDeal(parcel);
        }

        @Override
        public ActivateDeal[] newArray(int i) {
            return new ActivateDeal[i];
        }
    };

    public int getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(int currentDate) {
        this.currentDate = currentDate;
    }

    public int getDeal_actual_price() {
        return deal_actual_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeal_actual_price(int deal_actual_price) {
        this.deal_actual_price = deal_actual_price;
    }

    public int getDeal_discounted_price() {
        return deal_discounted_price;
    }

    public void setDeal_discounted_price(int deal_discounted_price) {
        this.deal_discounted_price = deal_discounted_price;
    }


    public String getDeal_id() {
        return deal_id;
    }

    public Map<String, Object> getClient_details() {
        return client_details;
    }

    public void setClient_details(Map<String, Object> client_details) {
        this.client_details = client_details;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getDeal_title() {
        return deal_title;
    }

    public void setDeal_title(String deal_title) {
        this.deal_title = deal_title;
    }

    public String getDeal_doc_id() {
        return deal_doc_id;
    }

    public void setDeal_doc_id(String deal_doc_id) {
        this.deal_doc_id = deal_doc_id;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }




}
