package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetail extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        String taskTitle = getIntent().getStringExtra(MainActivity.TASK_TITLE);
        TextView taskTitleID = findViewById(R.id.taskDetailTitle);
        taskTitleID.setText(taskTitle);

        String taskBody = getIntent().getStringExtra(MainActivity.TASK_BODY);
        TextView taskBodyID = findViewById(R.id.taskDetails);
        taskBodyID.setText(taskBody);

        String taskState = getIntent().getStringExtra(MainActivity.TASK_STATUS);
        TextView taskStateID = findViewById(R.id.taskDetailState);
        taskStateID.setText(taskState);
    }
}
