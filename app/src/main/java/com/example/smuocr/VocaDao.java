package com.example.smuocr;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VocaDao {

    @Insert
    void insert(Voca voca);

    @Update
    void update(Voca voca);

    @Delete
    void delete(Voca voca);

    @Query("SELECT * FROM voca_table")
    LiveData<List<Voca>> getAllvocas();



}

