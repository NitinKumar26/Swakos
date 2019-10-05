package com.swakos.model;

import com.google.firebase.Timestamp;

public class Cancel {
    private String reason;
    private Timestamp created_at;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
