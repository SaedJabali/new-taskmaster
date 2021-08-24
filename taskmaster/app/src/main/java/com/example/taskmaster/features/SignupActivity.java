package com.example.taskmaster.features;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText emailInput = findViewById(R.id.signup_emailInput);
        EditText usernameInput = findViewById(R.id.signup_usernameInput);
        EditText passwordInput = findViewById(R.id.signup_passwordInput);
        Button signupBtn = findViewById(R.id.signup_btn);
        Button LoginBtn = findViewById(R.id.signup_loginBtn);

        signupBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (!email.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                signUp(username, email, password);
            } else {
                Toast.makeText(SignupActivity.this,
                        "Please insert your info.??",
                        Toast.LENGTH_SHORT).show();
            }
        });
        LoginBtn.setOnClickListener(v -> {
            Intent goToLogin = new Intent(SignupActivity.this,
                    LoginActivity.class);
            startActivity(goToLogin);
        });

    }

    public void signUp(String username, String email, String password) {
        Amplify.Auth.signUp(
                username,
                password,
                AuthSignUpOptions.builder()
                        .userAttribute(AuthUserAttributeKey.email(), email)
                        .build(),
                success -> {
                    Log.i(TAG, "signUp successful: " + success.toString());
                    Intent goToVerification = new Intent(SignupActivity.this,
                            VerificationActivity.class);
                    goToVerification.putExtra("username", username);
                    goToVerification.putExtra("password", password);
                    startActivity(goToVerification);
                },
                error -> Log.e(TAG, "signUp failed: " + error.toString()));
    }
}