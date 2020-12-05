package com.example.androidfinal;

public class Ticket {
    private  long id;

    private String name,startDateTime,priceMin,priceMax,URL,image;


    public String getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(String priceMin) {
        this.priceMin = priceMin;
    }

    public String getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(String priceMax) {
        this.priceMax = priceMax;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Ticket(){

    }

    public Ticket(String name, String startDateTime, String priceMin,String priceMax, String URL, String image)
    {   this.id=0;
        this.name = name;
        this.startDateTime = startDateTime;
        this.priceMin =priceMin;
        this.priceMax=priceMax;
        this.URL = URL;
        this.image=image;

    }


    public Ticket(long id,String name, String startDateTime, String priceMin,String priceMax, String URL, String image)
    {
        this.id = id;
        this.name = name;
        this.startDateTime = startDateTime;
        this.priceMin =priceMin;
        this.priceMax=priceMax;
        this.URL = URL;
        this.image=image;


    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }



    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}




