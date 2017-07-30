package cmu.edu.expenser;


import com.google.api.client.util.DateTime;

import java.util.Date;

import static android.R.attr.name;

/**
 * Created by dandanshi on 29/07/2017.
 */

public class Item {
    private int total;
    private Date date;
    private String category;
    private int people;

    private static long counter = 0;

    public Item(){

    }

    public Item(int total, Date date, String category, int people){
        this.total = total;
        this.date = date;
        this.category = category;
        this.people = people;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setDate(Date total) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public int getTotal() {
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
}
