package com.example.das_proyecto2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.das_proyecto2.R;
import com.example.das_proyecto2.SessionManager;
import com.example.das_proyecto2.services.CreateUserService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //setup
        setup();

        if (ContextCompat.checkSelfPermission(AuthActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AuthActivity.this, new String[] { Manifest.permission.POST_NOTIFICATIONS }, 100);
        }
    }

    private void setup() {
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.emailText);
                EditText passwd = findViewById(R.id.passwordText);
                String emailText = email.getText().toString();
                String passwdText = passwd.getText().toString();
                if (!emailText.isEmpty() && !passwdText.isEmpty()) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passwdText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // User creation successful
                                //Toast.makeText(getApplicationContext(), "User created successfully!", Toast.LENGTH_SHORT).show();
                                SessionManager sessionManager = new SessionManager(AuthActivity.this);
                                sessionManager.saveSession("aaa", emailText);
                                // Get token for FCM
                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if (!task.isSuccessful()) {
                                                    return;
                                                }
                                                String token = task.getResult();
                                                // MAKE HTTP REQUEST TO UPLOAD EMAIL TO PYTHON API
                                                new CreateUserService().execute("http://34.136.205.220:8000/create_user", emailText, token);
                                                showHome(emailText, passwdText);
                                            }
                                        });
                            } else {
                                // User creation failed
                                Toast.makeText(getApplicationContext(), "User creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        Button loginButton = findViewById(R.id.logInButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.emailText);
                EditText passwd = findViewById(R.id.passwordText);
                String emailText = email.getText().toString();
                String passwdText = passwd.getText().toString();
                if (!emailText.isEmpty() && !passwdText.isEmpty()) {
                    System.out.println(email);
                    System.out.println(passwd);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailText, passwdText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // User creation successful
                                //Toast.makeText(getApplicationContext(), "User created successfully!", Toast.LENGTH_SHORT).show();
                                SessionManager sessionManager = new SessionManager(AuthActivity.this);
                                sessionManager.saveSession("aaa", emailText);
                                showHome(emailText, passwdText);
                            } else {
                                // User creation failed
                                Toast.makeText(getApplicationContext(), "User creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showHome(String email, String passwd) {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra("email", email);
        homeIntent.putExtra("passwd", passwd);
        startActivity(homeIntent);
        finish();
    }
}