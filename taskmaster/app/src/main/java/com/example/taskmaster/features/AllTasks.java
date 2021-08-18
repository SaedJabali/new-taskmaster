package com.example.taskmaster.features;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.Misc.TaskAdapter;
import com.example.taskmaster.R;

import java.util.List;


public class AllTasks extends AppCompatActivity {

    public static final String TASK_TITLE = "task_title";
    private static final String TAG = "All Tasks";

    private List<Task> taskList;
    TaskAdapter taskAdapter;
//    LinearLayoutManager linearLayoutManager;
//    private TaskDAO taskDao;
//    private AppDB db;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        configureAmplify();

        RecyclerView recyclerView = findViewById(R.id.listRecycler);
        taskList = MainActivity.getDataFromAmplify();

        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(MainActivity.TASK_TITLE, taskList.get(position).getTitle());
                goToDetailsIntent.putExtra(MainActivity.TASK_BODY, taskList.get(position).getDescription());
                goToDetailsIntent.putExtra(MainActivity.TASK_STATUS, taskList.get(position).getStatus());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {

            }

            @Override
            public void onUpdateItem(int position) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
//        db = Room.databaseBuilder(this,
//                AppDB.class,
//                MainActivity.TASK_DB)
//                .allowMainThreadQueries().build();
//
//        // can be pulled from the network or a local database
//        taskDao = db.taskDAO();
//        taskList = taskDao.findAll();
//
//        RecyclerView recyclerView = findViewById(R.id.listRecycler);
//
//        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskClickListener() {
//            @Override
//            public void onItemClicked(int position) {
//                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
//                goToDetailsIntent.putExtra(TASK_TITLE, taskList.get(position).getTitle());
//                goToDetailsIntent.putExtra(MainActivity.TASK_BODY, taskList.get(position).getBody());
//                goToDetailsIntent.putExtra(MainActivity.TASK_STATUS, taskList.get(position).getStatus());
//                startActivity(goToDetailsIntent);
//            }
//
//            @Override
//            public void onDeleteItem(int position) {
//                taskDao.deleteOne(taskList.get(position));
//                taskList.remove(position);
//                notifyDatasetChanged();
//                Toast.makeText(AllTasks.this, "Item deleted", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onUpdateItem(int position) {
//            }
//        });
//        linearLayoutManager = new LinearLayoutManager(
//                this, LinearLayoutManager.VERTICAL, false
//        );
//        recyclerView.setAdapter(taskAdapter);
//        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void configureAmplify() {
        // configure Amplify plugins
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "onCreate: Successfully initialized Amplify plugins");
        } catch (AmplifyException exception) {
            Log.e(TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
        }
    }

    @SuppressLint("NotifyDataSetChange")
    private void notifyDatasetChanged() {
        taskAdapter.notifyDataSetChanged();
    }

}