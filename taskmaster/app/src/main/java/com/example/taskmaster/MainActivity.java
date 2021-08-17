package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String TASK_DB = "task_db";
    public static final String TASK_TITLE = "taskTitle";
    public static final String TASK_BODY = "taskBody";
    public static final String TASK_STATUS = "taskStatus";

    AppDB taskDb;
    private List<Task> taskList;
    private TaskAdapter adapter;
    TaskDAO taskDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskDb = Room.databaseBuilder(
                this,
                AppDB.class,
                TASK_DB
        ).allowMainThreadQueries().build();

        taskDAO = taskDb.taskDAO();

        RecyclerView taskRecyclerView = findViewById(R.id.List_tasks);
//        taskList = new ArrayList<>();
//        taskList.add(new Task("Task 1","get some rest","new"));
//        taskList.add(new Task("Task 2","work on code challenge for today","assigned"));
//        taskList.add(new Task("Task 3","do lab work for today","in progress"));
//        taskList.add(new Task("Task 4","visit family","complete"));

        taskList = taskDAO.findAll();

        adapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(TASK_TITLE, taskList.get(position).getTitle());
                goToDetailsIntent.putExtra(TASK_BODY, taskList.get(position).getBody());
                goToDetailsIntent.putExtra(TASK_STATUS, taskList.get(position).getStatus());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {
                taskDAO.deleteOne(taskList.get(position));
                taskList.remove(position);
                listItemDeleted();
            }

            @Override
            public void onUpdateItem(int position) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        Button newTaskButton = findViewById(R.id.addTaskButton);
        newTaskButton.setOnClickListener(goToNewTaskCreator);

        Button allTasksButton = findViewById(R.id.allTasksButton);
        allTasksButton.setOnClickListener(goToAllTasks);

//        Button makeTaskDetailsButton = findViewById(R.id.makeTaskDetailsButton);
//        makeTaskDetailsButton.setOnClickListener(goToTaskDetail);
//
//        Button makeTaskDetailsButton1 = findViewById(R.id.makeTaskDetailsButton1);
//        makeTaskDetailsButton1.setOnClickListener(goToTaskDetail1);
//
//        Button makeTaskDetailsButton2 = findViewById(R.id.makeTaskDetailsButton2);
//        makeTaskDetailsButton2.setOnClickListener(goToTaskDetail2);


        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(goToSettings);

        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(linearLayoutManager);

        try {
            Amplify.configure(getApplicationContext());

            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preference.getString("username", "user") + "'s Tasks";
        TextView userLabel = findViewById(R.id.userTasksLabel);
        userLabel.setText(username);
    }

    private final View.OnClickListener goToNewTaskCreator = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(getBaseContext(), AddTask.class);
            startActivity(i);
        }
    };

    private final View.OnClickListener goToAllTasks = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(getBaseContext(), AllTasks.class);
            startActivity(i);
        }
    };

//    private final View.OnClickListener goToTaskDetail = new View.OnClickListener() {
//        public void onClick(View v) {
//            Button makeTaskDetailsButton = findViewById(R.id.makeTaskDetailsButton);
//            String buttonText = makeTaskDetailsButton.getText().toString();
//            Intent i = new Intent(getBaseContext(), TaskDetail.class);
//            i.putExtra(TASK_NAME, buttonText);
//            startActivity(i);
//        }
//    };
//
//    private final View.OnClickListener goToTaskDetail1 = new View.OnClickListener() {
//        public void onClick(View v) {
//            Button makeTaskDetailsButton1 = findViewById(R.id.makeTaskDetailsButton1);
//            String buttonText = makeTaskDetailsButton1.getText().toString();
//            Intent i = new Intent(getBaseContext(), TaskDetail.class);
//            i.putExtra(TASK_NAME, buttonText);
//            startActivity(i);
//        }
//    };
//
//    private final View.OnClickListener goToTaskDetail2 = new View.OnClickListener() {
//        public void onClick(View v) {
//            Button makeTaskDetailsButton2 = findViewById(R.id.makeTaskDetailsButton2);
//            String buttonText = makeTaskDetailsButton2.getText().toString();
//            Intent i = new Intent(getBaseContext(), TaskDetail.class);
//            i.putExtra(TASK_NAME, buttonText);
//            startActivity(i);
//        }
//    };

    private final View.OnClickListener goToSettings = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(getBaseContext(), Settings.class);
            startActivity(i);
        }
    };


    @SuppressLint("NotifyDataSetChanged")
    private void listItemDeleted() {
        adapter.notifyDataSetChanged();
    }

}