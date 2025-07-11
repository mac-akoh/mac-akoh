package com.example.mobilepaymentapp;

// import androidx.annotation.Nullable; // Not needed for now for QR onActivityResult
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast; // Added for placeholder messages

// Removed direct button imports as they will be in fragments

// Import necessary classes for BottomNavigationView
import com.example.mobilepaymentapp.ui.profile.ProfileActivity;
import com.example.mobilepaymentapp.ui.transactions.TransactionHistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

// Placeholder Fragment imports - these will be created in the next step
// For now, we'll just log/Toast for Home and Payments until fragments are made.
// import com.example.mobilepaymentapp.ui.home.HomeFragment;
// import com.example.mobilepaymentapp.ui.payments.PaymentsFragment;

// Import the actual Fragment classes
import com.example.mobilepaymentapp.ui.home.HomeFragment;
import com.example.mobilepaymentapp.ui.payments.PaymentsFragment;

// Services might be used by Fragments, so keeping them here for now or they can be instantiated in Fragments
import com.example.mobilepaymentapp.services.NfcService;
// import com.example.mobilepaymentapp.services.PaymentService; // Will be used in PaymentsFragment
// import com.example.mobilepaymentapp.services.QrCodeService; // Will be used in PaymentsFragment


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;

    // NFC related fields (might move to a specific fragment if NFC ops are localized)
    private NfcService nfcService;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] nfcIntentFilters;

    // Services might be passed to fragments or instantiated within them as needed
    // public PaymentService paymentService;
    // public QrCodeService qrCodeService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize services that might be needed globally or passed to fragments
        // paymentService = new PaymentService(this);
        // qrCodeService = new QrCodeService(this);
        nfcService = new NfcService(this); // For onNewIntent handling at Activity level

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        setupNfc(); // Setup NFC adapter and foreground dispatch intents for the activity

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment(), "HOME_FRAGMENT");
                setTitle("Home");
                return true;
            } else if (itemId == R.id.navigation_payments) {
                loadFragment(new PaymentsFragment(), "PAYMENTS_FRAGMENT");
                setTitle("Payments");
                return true;
            } else if (itemId == R.id.navigation_history) {
                Intent historyIntent = new Intent(MainActivity.this, TransactionHistoryActivity.class);
                startActivity(historyIntent);
                // Returning false keeps the previously selected item highlighted if you don't want
                // the 'History' tab to appear selected after launching an activity.
                // Alternatively, you might want to re-select the previous fragment or home.
                // For simplicity now, we just launch and don't change selection.
                return false;
            } else if (itemId == R.id.navigation_profile) {
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                return false;
            }
            return false;
        });

        // Load default fragment (Home)
        if (savedInstanceState == null) {
            // bottomNavigationView.setSelectedItemId(R.id.navigation_home); // This would trigger the listener
            // Directly load the home fragment to avoid double loading or race conditions with listener
            loadFragment(new HomeFragment(), "HOME_FRAGMENT");
            setTitle("Home");
            // Ensure the navigation_home item is visually selected if not already by loadFragment
             bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment_container, fragment, tag);
        // transaction.addToBackStack(null); // Optional: if you want to add fragment to back stack
        transaction.commit();
        Log.d(TAG, "Loaded fragment with tag: " + tag);
    }

    private void setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Log.w(TAG, "NFC not supported on this device.");
            return;
        }

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Failed to add NDEF MIME type.", e);
        }
        nfcIntentFilters = new IntentFilter[]{ndef};
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilters, null);
            Log.d(TAG, "NFC Foreground Dispatch Enabled");
        } else if (nfcAdapter != null && !nfcAdapter.isEnabled()) {
            Log.w(TAG, "NFC is disabled. Foreground dispatch not enabled.");
            // Consider prompting user to enable NFC here or in a more context-aware place
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
            Log.d(TAG, "NFC Foreground Dispatch Disabled");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent.getAction());
        if (nfcService != null && (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
            NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
            NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))) {
            nfcService.handleNfcTagReadPlaceholder(intent);
        }
    }

    // onActivityResult for QR scanning would typically be handled by the Fragment that initiates it,
    // or by the activity if the fragment uses `getActivity().startActivityForResult()`.
    // For now, keeping it commented out here.
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // ... QR handling logic ...
    }
    */
}
