package com.swakos.model;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class Deal {
    private String deal_id, deal_title, deal_doc_id;

    private Timestamp created_at;

    private HashMap<String, String> standard_terms;

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    private int deal_actual_price, deal_discounted_price;

    public int getDeal_actual_price() {
        return deal_actual_price;
    }


    public HashMap<String, String> getStandard_terms() {
        return standard_terms;
    }

    public void setStandard_terms(HashMap<String, String> standard_terms) {
        this.standard_terms = standard_terms;
    }

    public String getDeal_doc_id() {
        return deal_doc_id;
    }

    public void setDeal_doc_id(String deal_doc_id) {
        this.deal_doc_id = deal_doc_id;
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


    public Deal() {
    }

    public String getDeal_id() {
        return deal_id;
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

}