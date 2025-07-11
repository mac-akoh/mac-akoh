package com.example.mobilepaymentapp.ui.merchant;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.example.mobilepaymentapp.R;
import com.google.android.material.textfield.TextInputEditText;

public class MerchantRegistrationActivity extends AppCompatActivity {

    private TextInputEditText editTextBusinessName;
    private AutoCompleteTextView autoCompleteBusinessType;
    private TextInputEditText editTextBusinessAddress;
    private TextInputEditText editTextContactEmail;
    private TextInputEditText editTextContactPhone;
    private TextInputEditText editTextBankAccount; // Placeholder
    private Button buttonUploadDocumentPlaceholder;
    private Button buttonSubmitMerchantRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_registration);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Merchant Registration");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextBusinessName = findViewById(R.id.editTextBusinessName);
        autoCompleteBusinessType = findViewById(R.id.autoCompleteBusinessType);
        editTextBusinessAddress = findViewById(R.id.editTextBusinessAddress);
        editTextContactEmail = findViewById(R.id.editTextContactEmail);
        editTextContactPhone = findViewById(R.id.editTextContactPhone);
        editTextBankAccount = findViewById(R.id.editTextBankAccount);
        buttonUploadDocumentPlaceholder = findViewById(R.id.buttonUploadDocumentPlaceholder);
        buttonSubmitMerchantRegistration = findViewById(R.id.buttonSubmitMerchantRegistration);

        // Setup Business Type Dropdown (AutoCompleteTextView)
        String[] businessTypes = new String[]{"Sole Proprietorship", "Partnership", "LLC", "Corporation", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                businessTypes
        );
        autoCompleteBusinessType.setAdapter(adapter);

        buttonUploadDocumentPlaceholder.setOnClickListener(v -> {
            // Placeholder for document upload logic
            Toast.makeText(this, "Placeholder: Document Upload Clicked", Toast.LENGTH_SHORT).show();
        });

        buttonSubmitMerchantRegistration.setOnClickListener(v -> {
            // Placeholder for form validation and submission
            String businessName = editTextBusinessName.getText().toString();
            String businessType = autoCompleteBusinessType.getText().toString();
            // ... get other fields

            if (businessName.isEmpty() || businessType.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_LONG).show();
                return;
            }
            // Simulate submission
            Toast.makeText(this, "Merchant Registration Submitted (Placeholder)", Toast.LENGTH_LONG).show();
            finish(); // Close activity after "submission"
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
