package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.List;

public class AddTask extends AppCompatActivity {

    AppDB taskDb;
    TaskDAO taskDAO;
    private List<Task> taskList;

    private View.OnClickListener newTaskCreateListener = new View.OnClickListener() {
        public void onClick(View v) {

            String taskTitle =((EditText) findViewById(R.id.newTaskName)).getText().toString();
            String taskBody =((EditText) findViewById(R.id.newTaskBody)).getText().toString();
            String taskStatus =((EditText) findViewById(R.id.newStatus)).getText().toString();

            taskDAO.insertOne(new Task(taskTitle,taskBody,taskStatus));

            TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);            
            successLabel.setVisibility(View.VISIBLE);

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        taskDb = Room.databaseBuilder(this,
                AppDB.class,
                MainActivity.TASK_DB)
                .allowMainThreadQueries().build();

        // can be pulled from the network or a local database
        taskDAO = taskDb.taskDAO();
        taskList = taskDAO.findAll();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);
        successLabel.setVisibility(View.GONE);

        Button newTaskCreateButton = findViewById(R.id.newTaskSubmit);
        newTaskCreateButton.setOnClickListener(newTaskCreateListener);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

}