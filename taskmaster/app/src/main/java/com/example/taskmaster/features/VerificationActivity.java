package com.example.taskmaster.features;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.core.Amplify;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = "VerificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        EditText verificationCodeInput= findViewById(R.id.verificationCodeInput);
        Button verificationButton = findViewById(R.id.verificationCodeBtn);

        Intent intent = getIntent();
        String username = intent.getExtras().getString("username", "");
        String password = intent.getExtras().getString("password", "");

        verificationButton.setOnClickListener(v -> {
            String verification_number = verificationCodeInput.getText().toString();
            verification(username,verification_number,password);
        });
    }

    public void verification(String username, String confirmationNumber,String password) {
        Amplify.Auth.confirmSignUp(
                username,
                confirmationNumber,
                success -> {
                    Log.i(TAG, "verification: succeeded" + success.toString());
                    Intent goToSignIn = new Intent(VerificationActivity.this,
                            MainActivity.class);
                    goToSignIn.putExtra("username", username);
                    startActivity(goToSignIn);

                    silentSignIn(username, password);
                },
                error -> Log.e(TAG, "verification: failed" + error.toString()));
    }

    public void silentSignIn(String username, String password) {
        Amplify.Auth.signIn(
                username,
                password,
                success -> Log.i(TAG, "signIn: worked " + success.toString()),
                error -> Log.e(TAG, "signIn: failed" + error.toString()));
    }

}