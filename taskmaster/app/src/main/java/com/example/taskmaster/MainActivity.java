package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
//import com.example.taskmaster.Misc.AppDB;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.taskmaster.Misc.TaskAdapter;
//import com.example.taskmaster.Misc.TaskDAO;
import com.example.taskmaster.features.AddTask;
import com.example.taskmaster.features.AllTasks;
import com.example.taskmaster.features.LoginActivity;
import com.example.taskmaster.features.Settings;
import com.example.taskmaster.features.TaskDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String TASK_TITLE = "taskTitle";
    public static final String TASK_BODY = "taskBody";
    public static final String TASK_STATUS = "taskStatus";
    private static final String TAG = "Main Activity";
    private static final String AmpTAG = "Tutorial";


     static List<Task> taskList = new ArrayList<>();;
     static TaskAdapter adapter;
     static Handler handler;
     static Team teamData = null;
     static String teamNameData = null;
     static String currentUsername = null;
     static PinpointManager pinpointManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
//      Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }


        if (Amplify.Auth.getCurrentUser() != null) {
            Log.i(TAG, "Auth: " + Amplify.Auth.getCurrentUser().toString());
        } else {
            Log.i(TAG, "Auth:  no user " + Amplify.Auth.getCurrentUser());
            Intent goToLogin = new Intent(this, LoginActivity.class);
            startActivity(goToLogin);
        }

        handler = new Handler(Looper.getMainLooper(),
                message -> {
                    listItemDeleted();
                    return false;
                });

        RecyclerView taskRecyclerView = findViewById(R.id.List_tasks);

        adapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(TASK_TITLE, taskList.get(position).getTitle());
                goToDetailsIntent.putExtra(TASK_BODY, taskList.get(position).getDescription());
                goToDetailsIntent.putExtra(TASK_STATUS, taskList.get(position).getStatus());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {
//        taskDao.delete(taskList.get(position));
                Amplify.API.mutate(ModelMutation.delete(taskList.get(position)),
                        response -> Log.i(TAG, "item deleted from API:"),
                        error -> Log.e(TAG, "Delete failed", error)
                );
                taskList.remove(position);
                listItemDeleted();
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


        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(goToSettings);

        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(linearLayoutManager);

        getTaskDataFromAPI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Amplify.Auth.getCurrentUser() != null) {
            TextView userNameText = (findViewById(R.id.userTasksLabel));
            userNameText.setText(Amplify.Auth.getCurrentUser().getUsername() + "'s Tasks");
        } else {
            Intent goToLogin = new Intent(this, LoginActivity.class);
            startActivity(goToLogin);
        }


        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preference.getString("username", "user") + "'s Tasks";
        String teamName = "Your Team Name is: " + preference.getString("teamName", "Choose your team");
        teamNameData = preference.getString("teamName", null);
//    TextView userLabel = findViewById(R.id.userTasksLabel);
        TextView teamNameLabel = findViewById(R.id.teamTasksLabel);
//    userLabel.setText(username);
        teamNameLabel.setText(teamName);

        if (teamNameData != null) {
            getTeamDetailFromAPIByName();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            taskList.clear();
            Log.i(TAG, "-----selected team true-------- ");
            Log.i(TAG, teamNameData);
            getTaskDataFromAPIByTeam();
        }
    }

    private final View.OnClickListener goToNewTaskCreator = v -> {
        Intent i = new Intent(getBaseContext(), AddTask.class);
        startActivity(i);
    };

    private final View.OnClickListener goToAllTasks = v -> {
        Intent i = new Intent(getBaseContext(), AllTasks.class);
        startActivity(i);
    };
    private final View.OnClickListener goToSettings = v -> {
        Intent i = new Intent(getBaseContext(), Settings.class);
        startActivity(i);
    };

    public static void saveTaskToAPI(Task item) {
        Amplify.API.mutate(ModelMutation.create(item),
                success -> Log.i(TAG, "Saved item to api : " + success.getData().getTitle()),
                error -> Log.e(TAG, "Could not save item to API/dynamodb" + error.toString()));

    }


    public void getTaskDataFromAPI() {

        Amplify.API.query(ModelQuery.list(Task.class),
                response -> {
                    for (Task task : response.getData()) {
                        taskList.add(task);
                        Log.i(TAG, "get Task Data From Api: " + task.toString());
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "failed to get Task Data From Api: " + error.toString())
        );
    }


    public static void getTeamDetailFromAPIByName() {
        Amplify.API.query(ModelQuery.list(Team.class, Team.NAME.contains(teamNameData)),
                response -> {
                    for (Team teamDetail : response.getData()) {
                        Log.i(TAG, teamDetail.toString());
                        teamData = teamDetail;
                    }
                },
                error -> Log.e(TAG, "failed to get Team Detail : " + error.toString())
        );
    }

    public void getTaskDataFromAPIByTeam() {
        Amplify.API.query(ModelQuery.list(Task.class, Task.TEAM.contains(teamData.getId())),
                response -> {
                    for (Task task : response.getData()) {
                        Log.i(TAG, "Task Team ID: " + task.getTeam().getId());
                        Log.i(TAG, "Team ID: " + teamData.getId());
                        taskList.add(task);
                        Log.i(TAG, "get Task Data From API By Team: " + task.toString());
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "failed to get Task Data From API By Team: " + error.toString())
        );
    }

    public  void getCurrentUser() {
        AuthUser authUser = Amplify.Auth.getCurrentUser();
        currentUsername = authUser.getUsername();
        Log.i(TAG, "getCurrentUser: " + authUser.toString());
        Log.i(TAG, "getCurrentUser: username" + authUser.getUsername());
        Log.i(TAG, "getCurrentUser: userId" + authUser.getUserId());
    }


    public void logout(){
        Amplify.Auth.signOut(
                () ->{
                    Log.i("AuthQuickstart", "Signed out successfully");
                    Intent goToLogin = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(goToLogin);
                } ,
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            final String token = task.getResult();
                            Log.d(TAG, "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }
//    private void configureAmplify() {
//        // configure Amplify plugins
//        try {
//            Amplify.addPlugin(new AWSDataStorePlugin());
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.configure(getApplicationContext());
//            Log.i(TAG, "onCreate: Successfully initialized Amplify plugins");
//        } catch (AmplifyException exception) {
//            Log.e(TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
//        }
//    }


    @SuppressLint("NotifyDataSetChanged")
    private static void listItemDeleted() {
        adapter.notifyDataSetChanged();
    }

}