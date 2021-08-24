package com.example.taskmaster.features;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;

import androidx.room.Room;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddTask extends AppCompatActivity {
    static final String TAG = "Add Task";
    static String teamName = null;
    static Team teamData = null;
    static String pattern = "yyMMddHHmmssZ";
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    static String FileUploadName = simpleDateFormat.format(new Date());
    static String fileUploadExtention = null;
    static File uploadFile = null;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        configureAmplify();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button uploadFile = findViewById(R.id.uploadFileBtn);
        uploadFile.setOnClickListener(v1 -> getFileFromDevice());

        TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);
        successLabel.setVisibility(View.GONE);

        Button newTaskCreateButton = findViewById(R.id.newTaskSubmit);
        newTaskCreateButton.setOnClickListener(newTaskCreateListener);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    private View.OnClickListener newTaskCreateListener = v -> {

        String taskTitle = ((EditText) findViewById(R.id.newTaskName)).getText().toString();
        String taskBody = ((EditText) findViewById(R.id.newTaskBody)).getText().toString();
        String taskStatus = ((EditText) findViewById(R.id.newStatus)).getText().toString();

        getTeamDetailFromAPIByName(teamName);

        TextView successLabel = findViewById(R.id.newTaskSubmitSuccess);
        successLabel.setVisibility(View.VISIBLE);


        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Task item = Task.builder().
                title(taskTitle).
                description(taskBody).
                team(teamData).
                status(taskStatus).
                fileName(FileUploadName + "." + fileUploadExtention.split("/")[1]).
                build();

        saveTaskToAPI(item);
    };

    public void saveTaskToAPI(Task item) {
        Amplify.Storage.uploadFile(
                FileUploadName + "." + fileUploadExtention.split("/")[1],
                uploadFile,
                success -> {
                    Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
                },
                error -> {
                    Log.e(TAG, "uploadFileToS3: failed " + error.toString());
                }
        );
        Amplify.API.mutate(ModelMutation.create(item),
                success -> Log.i(TAG, "Saved item to api : " + success.getData()),
                error -> Log.e(TAG, "Could not save item to API/dynamodb" + error.toString()));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            fileUploadExtention = getContentResolver().getType(uri);

            Log.i(TAG, "onActivityResult: gg is " + fileUploadExtention);
            Log.i(TAG, "onActivityResult: returned from file explorer");
            Log.i(TAG, "onActivityResult: => " + data.getData());
            Log.i(TAG, "onActivityResult:  data => " + data.getType());

            uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
            } catch (Exception exception) {
                Log.e(TAG, "onActivityResult: file upload failed" + exception.toString());
            }

        }
    }

    private void getFileFromDevice() {
        Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("*/*");
        upload = Intent.createChooser(upload, "Choose a File");
        startActivityForResult(upload, 999); // deprecated
    }

    @SuppressLint("NonConstantResourceId")
    public void onClickRadioButton(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.team1:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 1");
                teamName = "team 1";
                break;
            case R.id.team2:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 2");
                teamName = "team 2";
                break;
            case R.id.team3:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 3");
                teamName = "team 3";
                break;
        }
    }


    public void getTeamDetailFromAPIByName(String name) {
        Amplify.API.query(
                ModelQuery.list(Team.class, Team.NAME.contains(name)),
                response -> {
                    for (Team teamDetail : response.getData()) {
                        Log.i(TAG, teamDetail.getName());
                        teamData = teamDetail;
                    }
                },
                error -> Log.e(TAG, "Query failure", error)
        );
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