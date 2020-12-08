package com.example.androidfinal;

public class DatabaseOBJ {
    private String country;
    private String date;
    private long id;

    //constructor
    public DatabaseOBJ() {}

    public DatabaseOBJ(String country, String date )
    {
        this.country = country;
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }


}
