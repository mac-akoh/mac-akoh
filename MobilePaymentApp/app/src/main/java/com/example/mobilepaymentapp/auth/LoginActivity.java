package com.example.mobilepaymentapp.auth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilepaymentapp.MainActivity;
import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.services.AuthService;
import com.example.mobilepaymentapp.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputLayout textFieldEmailLogin;
    private TextInputEditText editTextEmailLogin;
    private TextInputLayout textFieldPasswordLogin;
    private TextInputEditText editTextPasswordLogin;
    private Button buttonLogin;
    private TextView textViewForgotPasswordLink;
    private TextView textViewRegisterLink;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = new AuthService();

        // Initialize UI elements
        textFieldEmailLogin = findViewById(R.id.textFieldEmailLogin);
        editTextEmailLogin = findViewById(R.id.editTextEmailLogin);
        textFieldPasswordLogin = findViewById(R.id.textFieldPasswordLogin);
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPasswordLink = findViewById(R.id.textViewForgotPasswordLink);
        textViewRegisterLink = findViewById(R.id.textViewRegisterLink);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        textViewRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        textViewForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ForgotPasswordActivity
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        // Check if user is already logged in (basic check for now, replace with actual session management)
        // For example, if using Firebase:
        // if (FirebaseAuth.getInstance().getCurrentUser() != null) {
        //     navigateToMain();
        // }
    }

    private void loginUser() {
        textFieldEmailLogin.setError(null);
        textFieldPasswordLogin.setError(null);

        String email = editTextEmailLogin.getText().toString().trim();
        String password = editTextPasswordLogin.getText().toString().trim();

        if (!ValidationUtils.isValidEmail(email)) {
            textFieldEmailLogin.setError("Invalid email address.");
            return;
        }

        if (password.isEmpty()) { // Basic check, could use ValidationUtils.isValidPassword if rules are the same
            textFieldPasswordLogin.setError("Password cannot be empty.");
            return;
        }

        Log.d(TAG, "Attempting to login user: " + email);
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
        // Add ProgressBar visibility toggle here in a real app

        authService.loginUser(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "User login successful: " + email);
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                navigateToMain();
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "User login failed: " + message);
                Toast.makeText(LoginActivity.this, "Login failed: " + message, Toast.LENGTH_LONG).show();
                // Hide ProgressBar here
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
