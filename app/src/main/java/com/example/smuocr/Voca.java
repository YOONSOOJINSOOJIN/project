package com.example.smuocr;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "voca_table")
public class Voca {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String english;

    private String mean;

    public Voca(String english, String mean) {
        this.english = english;
        this.mean = mean;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEnglish() {
        return english;
    }

    public String getMean() {
        return mean;
    }
}
