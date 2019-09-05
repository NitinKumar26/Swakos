package com.swakos.model;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Client {
    private String name, address, id, banner_url, documentID, client_desc;
    private Timestamp created_at;
    private long contact_number;
    private Map<String, Long> ratings;
    private Map<String, HashMap<String, Object>> special_deals;
    private HashMap<String, String> standard_terms;

    public HashMap<String, String> getStandard_terms() {
        return standard_terms;
    }

    public void setStandard_terms(HashMap<String, String> standard_terms) {
        this.standard_terms = standard_terms;
    }

    public String getDocumentID() {
        return documentID;
    }

    public String getClient_desc() {
        return client_desc;
    }

    public void setClient_desc(String client_desc) {
        this.client_desc = client_desc;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Map<String, Long> getRatings() {
        return ratings;
    }

    public Map<String, HashMap<String, Object>> getSpecial_deals() {
        return special_deals;
    }

    public void setSpecial_deals(Map<String, HashMap<String, Object>> special_deals) {
        this.special_deals = special_deals;
    }

    public String getBanner_url() {
        return banner_url;
    }

    public void setBanner_url(String mBannerURL) {
        this.banner_url = mBannerURL;
    }

    public void setRatings(Map<String, Long> ratings) {
        this.ratings = ratings;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Client() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getContact_number() {
        return contact_number;
    }

    public void setContact_number(long contact_number) {
        this.contact_number = contact_number;
    }
}
