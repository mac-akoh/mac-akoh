package com.example.mobilepaymentapp.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.auth.LoginActivity;
// import com.example.mobilepaymentapp.MainActivity; // If implementing check for logged-in user

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MILLIS = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Optional: Make splash screen full screen (hides status bar)
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //         WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // TODO: Check if user is already logged in.
            // If yes, navigate to MainActivity.
            // If no, navigate to LoginActivity.
            // For now, always navigate to LoginActivity as persistent login is not implemented.

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish SplashActivity so user can't navigate back to it
        }, SPLASH_DELAY_MILLIS);
    }
}
