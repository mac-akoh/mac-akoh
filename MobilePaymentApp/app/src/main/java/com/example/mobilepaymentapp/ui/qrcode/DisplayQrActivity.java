package com.example.mobilepaymentapp.ui.qrcode;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.services.QrCodeService;

public class DisplayQrActivity extends AppCompatActivity {

    public static final String EXTRA_QR_DATA = "com.example.mobilepaymentapp.QR_DATA";

    private ImageView imageViewQrCode;
    private TextView textViewQrData;
    private QrCodeService qrCodeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qr);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Scan QR Code");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageViewQrCode = findViewById(R.id.imageViewQrCode);
        textViewQrData = findViewById(R.id.textViewQrData); // Assuming you have a TextView to show the data
        qrCodeService = new QrCodeService(this);

        String qrData = getIntent().getStringExtra(EXTRA_QR_DATA);

        if (qrData != null && !qrData.isEmpty()) {
            textViewQrData.setText("QR Data: " + qrData); // Display the data used for QR
            // Generate and display QR code
            // Dimensions for QR code - make them reasonably large for display
            int qrCodeSize = 800; // pixels
            Bitmap qrBitmap = qrCodeService.generateQrCodeBitmap(qrData, qrCodeSize, qrCodeSize);
            if (qrBitmap != null) {
                imageViewQrCode.setImageBitmap(qrBitmap);
            } else {
                Toast.makeText(this, "Could not generate QR code image.", Toast.LENGTH_SHORT).show();
                // Optionally set a placeholder error image
            }
        } else {
            Toast.makeText(this, "No data provided for QR code generation.", Toast.LENGTH_SHORT).show();
            textViewQrData.setText("Error: No data for QR Code.");
            // Optionally set a placeholder error image or finish activity
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
