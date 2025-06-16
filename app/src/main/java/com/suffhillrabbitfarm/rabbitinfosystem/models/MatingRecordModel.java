package com.suffhillrabbitfarm.rabbitinfosystem.models;

public class MatingRecordModel {
    private String id;
    private String rabbit_id;
    private String male_rabbit_id;
    private String mating_date;
    private String expected_delivery_date;
    private String litter_size;
    private String live_births;
    private String stillbirths;
    private String notes;
    private String status;
    private String created_at;

    public MatingRecordModel() {
    }

    public MatingRecordModel(String id, String rabbit_id, String male_rabbit_id, String mating_date,
            String expected_delivery_date, String litter_size, String live_births,
            String stillbirths, String notes, String status, String created_at) {
        this.id = id;
        this.rabbit_id = rabbit_id;
        this.male_rabbit_id = male_rabbit_id;
        this.mating_date = mating_date;
        this.expected_delivery_date = expected_delivery_date;
        this.litter_size = litter_size;
        this.live_births = live_births;
        this.stillbirths = stillbirths;
        this.notes = notes;
        this.status = status;
        this.created_at = created_at;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRabbit_id() {
        return rabbit_id;
    }

    public void setRabbit_id(String rabbit_id) {
        this.rabbit_id = rabbit_id;
    }

    public String getMale_rabbit_id() {
        return male_rabbit_id;
    }

    public void setMale_rabbit_id(String male_rabbit_id) {
        this.male_rabbit_id = male_rabbit_id;
    }

    public String getMating_date() {
        return mating_date;
    }

    public void setMating_date(String mating_date) {
        this.mating_date = mating_date;
    }

    public String getExpected_delivery_date() {
        return expected_delivery_date;
    }

    public void setExpected_delivery_date(String expected_delivery_date) {
        this.expected_delivery_date = expected_delivery_date;
    }

    public String getLitter_size() {
        return litter_size;
    }

    public void setLitter_size(String litter_size) {
        this.litter_size = litter_size;
    }

    public String getLive_births() {
        return live_births;
    }

    public void setLive_births(String live_births) {
        this.live_births = live_births;
    }

    public String getStillbirths() {
        return stillbirths;
    }

    public void setStillbirths(String stillbirths) {
        this.stillbirths = stillbirths;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}