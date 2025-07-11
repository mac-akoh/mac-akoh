package com.example.mobilepaymentapp.ui.support;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mobilepaymentapp.R;

public class SupportActivity extends AppCompatActivity {

    private TextView textViewFaqContentPlaceholder;
    private TextView textViewContactEmail;
    private TextView textViewContactPhone;
    private Button buttonContactSupport;

    // Placeholder contact details
    private static final String SUPPORT_EMAIL = "support@examplemobilepayment.app";
    private static final String SUPPORT_PHONE = "tel:+18005550199";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Help & Support");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textViewFaqContentPlaceholder = findViewById(R.id.textViewFaqContentPlaceholder);
        textViewContactEmail = findViewById(R.id.textViewContactEmail);
        textViewContactPhone = findViewById(R.id.textViewContactPhone);
        buttonContactSupport = findViewById(R.id.buttonContactSupport);

        // Set placeholder text (though it's also in XML, could be dynamic)
        // textViewFaqContentPlaceholder.setText("...");
        textViewContactEmail.setText("Email: " + SUPPORT_EMAIL);
        textViewContactPhone.setText("Phone: " + SUPPORT_PHONE.replace("tel:", ""));


        textViewContactEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + SUPPORT_EMAIL));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Mobile Payment App");
            try {
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(SupportActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });

        textViewContactPhone.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse(SUPPORT_PHONE));
            try {
                startActivity(dialIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(SupportActivity.this, "No phone clients installed.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonContactSupport.setOnClickListener(v -> {
            // Placeholder for launching a live chat, support ticket system, or another contact method
            Toast.makeText(this, "Placeholder: Contacting Live Support...", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
