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
import java.util.Objects;


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
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
    }

}