package com.example.taskmaster;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDAO {

    @Insert
    void insertOne(Task task);

    @Query("SELECT * FROM task WHERE task_title LIKE :name")
    Task findByName(String name);

    @Query("SELECT * FROM task")
    List<Task> findAll();

    @Delete
    void deleteOne(Task task);
}
