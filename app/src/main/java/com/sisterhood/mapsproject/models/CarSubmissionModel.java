package com.sisterhood.mapsproject.models;

public class CarSubmissionModel {

    private String NameOfDriver, carNumber, rating, incident;

    public CarSubmissionModel(String nameOfDriver, String carNumber, String rating, String incident) {
        NameOfDriver = nameOfDriver;
        this.carNumber = carNumber;
        this.rating = rating;
        this.incident = incident;
    }

    public CarSubmissionModel() {
    }

    public String getNameOfDriver() {
        return NameOfDriver;
    }

    public void setNameOfDriver(String nameOfDriver) {
        NameOfDriver = nameOfDriver;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIncident() {
        return incident;
    }

    public void setIncident(String incident) {
        this.incident = incident;
    }
}
