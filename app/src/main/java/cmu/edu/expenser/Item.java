package cmu.edu.expenser;

import java.util.Date;

/**
 * Created by dandanshi on 29/07/2017.
 */

public class Item {
    private String userId;
    private double total;
    private Date date;
    private String category;
    private int people;
    private double average;
    

    public Item(){

    }

    public Item(String userId, double total, Date date, String category, int people){
        this.userId = userId;
        this.total = total;
        this.date = date;
        this.category = category;
        this.people = people;
        this.average = total / people;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTotal(int total) {
        this.total = total;
        this.average = total / this.people;
    }

    public void setDate(Date total) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPeople(int people) {
        this.people = people;
        this.average = this.total / people;
    }

    public String getUserId() {
        return userId;
    }

    public double getTotal() {
        return total;
    }

    public Date getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public int getPeople() {
        return people;
    }

    public double getAverage() {
        return average;
    }
}
