package com.suffhillrabbitfarm.rabbitinfosystem.models;

public class WeightRecordModel {
    private String id;
    private String rabbit_id;
    private String weight;
    private String date;
    private String notes;
    private String recorded_by;
    private String created_at;

    public WeightRecordModel() {
    }

    public WeightRecordModel(String id, String rabbit_id, String weight, String date, String notes, String recorded_by,
            String created_at) {
        this.id = id;
        this.rabbit_id = rabbit_id;
        this.weight = weight;
        this.date = date;
        this.notes = notes;
        this.recorded_by = recorded_by;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRecorded_by() {
        return recorded_by;
    }

    public void setRecorded_by(String recorded_by) {
        this.recorded_by = recorded_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}