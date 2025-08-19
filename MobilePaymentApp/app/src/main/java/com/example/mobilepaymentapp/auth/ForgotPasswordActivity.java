package com.example.mobilepaymentapp.auth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.services.AuthService;
import com.example.mobilepaymentapp.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    private TextInputLayout textFieldEmailReset;
    private TextInputEditText editTextEmailReset;
    private Button buttonSendResetLink;
    private TextView textViewBackToLoginLink;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authService = new AuthService();

        textFieldEmailReset = findViewById(R.id.textFieldEmailReset);
        editTextEmailReset = findViewById(R.id.editTextEmailReset);
        buttonSendResetLink = findViewById(R.id.buttonSendResetLink);
        textViewBackToLoginLink = findViewById(R.id.textViewBackToLoginLink);

        buttonSendResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetLink();
            }
        });

        textViewBackToLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                // Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                // startActivity(intent);
                finish(); // Simply finish this activity to go back to the previous one (LoginActivity)
            }
        });
    }

    private void sendPasswordResetLink() {
        textFieldEmailReset.setError(null);
        String email = editTextEmailReset.getText().toString().trim();

        if (!ValidationUtils.isValidEmail(email)) {
            textFieldEmailReset.setError("Invalid email address.");
            return;
        }

        Log.d(TAG, "Attempting to send password reset link to: " + email);
        Toast.makeText(this, "Sending link...", Toast.LENGTH_SHORT).show();
        // Show ProgressBar

        authService.sendPasswordResetEmail(email, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Password reset link sent successfully to: " + email);
                Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                // Optionally navigate back to LoginActivity or show a success message and stay
                // finish(); // To go back to LoginActivity
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Failed to send password reset link: " + message);
                Toast.makeText(ForgotPasswordActivity.this, "Failed: " + message, Toast.LENGTH_LONG).show();
                // Hide ProgressBar
            }
        });
    }
}
