package com.example.taskmaster;

import androidx.room.*;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "task_title")
    private final String title;

    @ColumnInfo(name = "task_body")
    private final String body;

    @ColumnInfo(name = "task_status")
    private final String status;


    public Task(String title, String body, String status) {
        this.title = title;
        this.body = body;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}