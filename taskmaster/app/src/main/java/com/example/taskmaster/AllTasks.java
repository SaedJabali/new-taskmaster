package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;


public class AllTasks extends AppCompatActivity {

    public static final String TASK_TITLE = "task_title";
    private static final String TAG = "AllTasks";

    private List<Task> taskList;
    TaskAdapter taskAdapter;
    LinearLayoutManager linearLayoutManager;
    private TaskDAO taskDao;
    private AppDB db;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        db = Room.databaseBuilder(this,
                AppDB.class,
                MainActivity.TASK_DB)
                .allowMainThreadQueries().build();

        // can be pulled from the network or a local database
        taskDao = db.taskDAO();
        taskList = taskDao.findAll();

        RecyclerView recyclerView = findViewById(R.id.listRecycler);

        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(TASK_TITLE, taskList.get(position).getTitle());
                goToDetailsIntent.putExtra(MainActivity.TASK_BODY, taskList.get(position).getBody());
                goToDetailsIntent.putExtra(MainActivity.TASK_STATUS, taskList.get(position).getStatus());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {
                taskDao.deleteOne(taskList.get(position));
                taskList.remove(position);
                notifyDatasetChanged();
                Toast.makeText(AllTasks.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpdateItem(int position) {
            }
        });
        linearLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        );
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void notifyDatasetChanged() {
        taskAdapter.notifyDataSetChanged();
    }

}