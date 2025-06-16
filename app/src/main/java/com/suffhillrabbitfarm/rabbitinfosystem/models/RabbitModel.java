package com.suffhillrabbitfarm.rabbitinfosystem.models;

public class RabbitModel {
    String rabbit_id, breed, color, gender, weight, dob, father_id, mother_id, observations, cage_number;

    public String getRabbit_id() {
        return rabbit_id;
    }

    public void setRabbit_id(String rabbit_id) {
        this.rabbit_id = rabbit_id;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFather_id() {
        return father_id;
    }

    public void setFather_id(String father_id) {
        this.father_id = father_id;
    }

    public String getMother_id() {
        return mother_id;
    }

    public void setMother_id(String mother_id) {
        this.mother_id = mother_id;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getCage_number() {
        return cage_number;
    }

    public void setCage_number(String cage_number) {
        this.cage_number = cage_number;
    }
}
