package com.suffhillrabbitfarm.rabbitinfosystem.models;

public class HealthRecordModel {
    private String id;
    private String rabbit_id;
    private String record_date;
    private String health_status;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String medication;
    private String dosage;
    private String vet_name;
    private String vet_contact;
    private String cost;
    private String notes;
    private String created_at;

    public HealthRecordModel() {
    }

    public HealthRecordModel(String id, String rabbit_id, String record_date, String health_status,
            String symptoms, String diagnosis, String treatment, String medication,
            String dosage, String vet_name, String vet_contact, String cost,
            String notes, String created_at) {
        this.id = id;
        this.rabbit_id = rabbit_id;
        this.record_date = record_date;
        this.health_status = health_status;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.medication = medication;
        this.dosage = dosage;
        this.vet_name = vet_name;
        this.vet_contact = vet_contact;
        this.cost = cost;
        this.notes = notes;
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

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public String getHealth_status() {
        return health_status;
    }

    public void setHealth_status(String health_status) {
        this.health_status = health_status;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getVet_name() {
        return vet_name;
    }

    public void setVet_name(String vet_name) {
        this.vet_name = vet_name;
    }

    public String getVet_contact() {
        return vet_contact;
    }

    public void setVet_contact(String vet_contact) {
        this.vet_contact = vet_contact;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}