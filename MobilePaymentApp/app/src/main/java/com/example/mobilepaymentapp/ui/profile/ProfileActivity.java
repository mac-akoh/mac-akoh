package com.example.mobilepaymentapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.auth.LoginActivity;
// Import your AuthService or Firebase Auth instance if needed for actual user data and logout
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewUserName;
    private TextView textViewUserEmail;
    private Button buttonLogout;

    // private FirebaseAuth mAuth; // Example if using Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // mAuth = FirebaseAuth.getInstance(); // Example

        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        buttonLogout = findViewById(R.id.buttonLogout);
        // Potentially add a button here to navigate to SupportActivity
        // Example: Button buttonGoToSupport = findViewById(R.id.buttonGoToSupportFromProfile);

        loadUserProfile();

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // if (buttonGoToSupport != null) {
        //    buttonGoToSupport.setOnClickListener(v -> {
        //        Intent intent = new Intent(ProfileActivity.this, com.example.mobilepaymentapp.ui.support.SupportActivity.class);
        //        startActivity(intent);
        //    });
        // }
    }

    private void loadUserProfile() {
        // In a real app, fetch user data from AuthService or Firebase
        // FirebaseUser currentUser = mAuth.getCurrentUser();
        // if (currentUser != null) {
        //    textViewUserName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "N/A");
        //    textViewUserEmail.setText(currentUser.getEmail());
        // } else {
        //    // Handle case where user data is not available (should not happen if ProfileActivity is protected)
        //    textViewUserName.setText("Guest User (Error)");
        //    textViewUserEmail.setText("N/A");
        // }

        // For now, using placeholder values already in XML, but could set them here too
        // textViewUserName.setText("Placeholder Name from Java");
        // textViewUserEmail.setText("placeholder.email@example.com from Java");
    }

    private void logoutUser() {
        // Perform actual logout operation with AuthService or Firebase
        // mAuth.signOut(); // Example for Firebase

        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

        // Navigate back to LoginActivity and clear activity stack
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
