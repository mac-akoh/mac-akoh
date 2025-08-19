package com.example.mobilepaymentapp.ui.payments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.services.NfcService;
import com.example.mobilepaymentapp.services.PaymentService;
import com.example.mobilepaymentapp.services.QrCodeService;
import com.example.mobilepaymentapp.ui.qrcode.DisplayQrActivity;
import com.google.android.material.button.MaterialButton;

public class PaymentsFragment extends Fragment {

    private static final String TAG = "PaymentsFragment";

    private MaterialButton buttonMakePaymentSimulated;
    private MaterialButton buttonGenerateMyQr;
    private MaterialButton buttonScanQrToPay;
    private MaterialButton buttonNfcTapToPay;

    private PaymentService paymentService;
    private QrCodeService qrCodeService;
    private NfcService nfcService;


    public PaymentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // It's generally safer to initialize context-dependent objects in onAttach or onViewCreated
        // to ensure the Fragment is fully attached to its Context/Activity.
        // However, for services that just need a Context, onCreate can be okay if using requireContext().
        paymentService = new PaymentService(requireContext());
        qrCodeService = new QrCodeService(requireContext());
        nfcService = new NfcService(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonMakePaymentSimulated = view.findViewById(R.id.buttonMakePaymentSimulated);
        buttonGenerateMyQr = view.findViewById(R.id.buttonGenerateMyQr);
        buttonScanQrToPay = view.findViewById(R.id.buttonScanQrToPay);
        buttonNfcTapToPay = view.findViewById(R.id.buttonNfcTapToPay);

        buttonMakePaymentSimulated.setOnClickListener(v -> {
            String mockPhoneNumber = "237670000000"; // Example
            double mockAmount = 10.50; // Example
            paymentService.processMtnMomoPayment(mockPhoneNumber, mockAmount, new PaymentService.PaymentCallback() {
                @Override
                public void onSuccess(String transactionId, String message) {
                    Log.i(TAG, "Payment Success: " + transactionId + " - " + message);
                    Toast.makeText(getContext(), "Payment Success: " + message, Toast.LENGTH_LONG).show();
                }
                @Override
                public void onError(String message) {
                    Log.e(TAG, "Payment Error: " + message);
                    Toast.makeText(getContext(), "Payment Error: " + message, Toast.LENGTH_LONG).show();
                }
                @Override
                public void onPending(String transactionId, String message) {
                    Log.i(TAG, "Payment Pending: " + transactionId + " - " + message);
                    Toast.makeText(getContext(), "Payment Pending: " + message, Toast.LENGTH_LONG).show();
                }
            });
        });

        buttonGenerateMyQr.setOnClickListener(v -> {
            String qrData = "{\"userId\":\"user123\", \"amount\":2500.00, \"currency\":\"XAF\", \"type\":\"PAYMENT_REQUEST\"}"; // Amount adjusted for XAF
            Intent intent = new Intent(getActivity(), DisplayQrActivity.class);
            intent.putExtra(DisplayQrActivity.EXTRA_QR_DATA, qrData);
            startActivity(intent);
        });

        buttonScanQrToPay.setOnClickListener(v -> {
            // Note: QR scan result is handled in MainActivity's onActivityResult for now.
            // For better encapsulation, MainActivity could delegate onActivityResult to the current visible fragment,
            // or the fragment itself could use ActivityResultLauncher.
            qrCodeService.startQrCodeScanner(requireActivity());
        });

        buttonNfcTapToPay.setOnClickListener(v -> {
            if (!nfcService.isNfcSupported()) {
                Toast.makeText(getContext(), "NFC not supported on this device.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!nfcService.isNfcEnabled()) {
                Toast.makeText(getContext(), "NFC is disabled. Please enable it.", Toast.LENGTH_SHORT).show();
                nfcService.requestNfcEnable();
                return;
            }
            nfcService.startHcePaymentServicePlaceholder();
            Toast.makeText(getContext(), "NFC Tap-to-Pay (Placeholder). App ready.", Toast.LENGTH_LONG).show();
        });
    }
    // Note: If PaymentsFragment were to handle the QR scan result directly (e.g. using ActivityResultLauncher),
    // the onActivityResult logic from MainActivity would move here or be adapted.
}
