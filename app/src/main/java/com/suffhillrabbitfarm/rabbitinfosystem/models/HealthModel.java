package com.suffhillrabbitfarm.rabbitinfosystem.models;

public class HealthModel {
   String currentStatus;
   String slaughterDate;
   String deathDate;
   String deathCause;

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getSlaughterDate() {
        return slaughterDate;
    }

    public void setSlaughterDate(String slaughterDate) {
        this.slaughterDate = slaughterDate;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getDeathCause() {
        return deathCause;
    }

    public void setDeathCause(String deathCause) {
        this.deathCause = deathCause;
    }
}
