package com.example.taskmaster.Misc;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.taskmaster.model.Task;


@Database(entities = {Task.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract TaskDAO taskDAO();
}
