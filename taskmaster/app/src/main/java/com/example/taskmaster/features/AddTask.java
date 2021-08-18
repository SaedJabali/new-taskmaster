package com.example.taskmaster.features;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import androidx.room.Room;

import java.util.List;

public class AddTask extends AppCompatActivity {
    private static final String TAG = "Add Task";

//    AppDB taskDb;
//    TaskDAO taskDAO;
//    private List<Task> taskList;

    private View.OnClickListener newTaskCreateListener = new View.OnClickListener() {
        public void onClick(View v) {

            String taskTitle =((EditText) findViewById(R.id.newTaskName)).getText().toString();
            String taskBody =((EditText) findViewById(R.id.newTaskBody)).getText().toString();
            String taskStatus =((EditText) findViewById(R.id.newStatus)).getText().toString();

//            taskDAO.insertOne(new Task(taskTitle,taskBody,taskStatus));
            MainActivity.saveDataToAmplify(taskTitle, taskBody, taskStatus);

            TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);            
            successLabel.setVisibility(View.VISIBLE);

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        taskDb = Room.databaseBuilder(this,
//                AppDB.class,
//                MainActivity.TASK_DB)
//                .allowMainThreadQueries().build();
//
//        // can be pulled from the network or a local database
//        taskDAO = taskDb.taskDAO();
//        taskList = taskDAO.findAll();

configureAmplify();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);
        successLabel.setVisibility(View.GONE);

        Button newTaskCreateButton = findViewById(R.id.newTaskSubmit);
        newTaskCreateButton.setOnClickListener(newTaskCreateListener);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
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
}