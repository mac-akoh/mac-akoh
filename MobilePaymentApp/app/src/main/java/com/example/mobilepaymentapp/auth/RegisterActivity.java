package com.example.mobilepaymentapp.auth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilepaymentapp.MainActivity; // Assuming successful registration leads here for now
import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.services.AuthService;
import com.example.mobilepaymentapp.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private TextInputLayout textFieldEmail;
    private TextInputEditText editTextEmail;
    private TextInputLayout textFieldPassword;
    private TextInputEditText editTextPassword;
    private TextInputLayout textFieldConfirmPassword;
    private TextInputEditText editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewLoginLink;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authService = new AuthService();

        // Initialize UI elements
        textFieldEmail = findViewById(R.id.textFieldEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        textFieldPassword = findViewById(R.id.textFieldPassword);
        editTextPassword = findViewById(R.id.editTextPassword);
        textFieldConfirmPassword = findViewById(R.id.textFieldConfirmPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optional: finish RegisterActivity so user can't go back
            }
        });
    }

    private void registerUser() {
        // Clear previous errors
        textFieldEmail.setError(null);
        textFieldPassword.setError(null);
        textFieldConfirmPassword.setError(null);

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validate input
        if (!ValidationUtils.isValidEmail(email)) {
            textFieldEmail.setError("Invalid email address.");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            textFieldPassword.setError("Password must be at least 6 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            textFieldConfirmPassword.setError("Passwords do not match.");
            return;
        }

        // Show progress (e.g., ProgressBar - not implemented yet)
        // For now, just log and show Toast
        Log.d(TAG, "Attempting to register user: " + email);
        Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show();

        authService.registerUser(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "User registration successful: " + email);
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                // Navigate to MainActivity or LoginActivity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class); // Or LoginActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "User registration failed: " + message);
                // Show error message to user (e.g., in a dialog or a TextView)
                // For now, just a Toast
                Toast.makeText(RegisterActivity.this, "Registration failed: " + message, Toast.LENGTH_LONG).show();
                // Potentially set error on specific fields if applicable, e.g.,
                // if (message.contains("email already exists")) {
                //     textFieldEmail.setError(message);
                // }
            }
        });
    }
}
