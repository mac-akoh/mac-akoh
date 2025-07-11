package com.example.mobilepaymentapp.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.UUID;

public class PaymentService {

    public interface PaymentCallback {
        void onSuccess(String transactionId, String message);
        void onError(String message);
        void onPending(String transactionId, String message);
    }

    private Context context; // For showing Toasts or other UI feedback if needed directly

    public PaymentService(Context context) {
        this.context = context;
    }

    /**
     * Placeholder for initiating a credit/debit card payment (e.g., via Stripe).
     * In a real scenario, this would involve interacting with the Stripe SDK,
     * collecting card details securely (often using Stripe's UI elements),
     * and communicating with a backend to create a PaymentIntent.
     */
    public void processCardPayment(String cardNumber, String expiryDate, String cvc, double amount, PaymentCallback callback) {
        // Simulate network delay and processing
        Toast.makeText(context, "Processing card payment for XAF " + amount + "...", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (cardNumber.startsWith("4")) { // Simulate success for Visa
                String transactionId = "stripe_txn_" + UUID.randomUUID().toString();
                callback.onSuccess(transactionId, "Card payment successful.");
            } else if (cardNumber.startsWith("5")) { // Simulate pending for Mastercard
                 String transactionId = "stripe_txn_" + UUID.randomUUID().toString();
                callback.onPending(transactionId, "Card payment is pending confirmation.");
            }
            else {
                callback.onError("Card payment failed: Invalid card details.");
            }
        }, 2000);
    }

    /**
     * Placeholder for initiating an MTN Mobile Money payment.
     * Real integration would involve an SDK or API calls, potentially USSD prompts
     * or redirecting to an MTN payment page/app.
     */
    public void processMtnMomoPayment(String phoneNumber, double amount, PaymentCallback callback) {
        Toast.makeText(context, "Initiating MTN Momo payment for XAF " + amount + " to " + phoneNumber + "...", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Simulate different outcomes
            if (phoneNumber.length() > 5 && amount > 0) { // Basic validation
                String transactionId = "mtn_momo_" + UUID.randomUUID().toString();
                 // Mobile money payments often go into a pending state first
                callback.onPending(transactionId, "MTN Momo payment initiated. Awaiting confirmation from user via USSD prompt or app.");
            } else {
                callback.onError("MTN Momo payment failed: Invalid phone number or amount.");
            }
        }, 2500);
    }

    /**
     * Placeholder for initiating an Orange Money payment.
     * Similar to MTN Momo, real integration would use an SDK or specific APIs.
     */
    public void processOrangeMoneyPayment(String phoneNumber, double amount, PaymentCallback callback) {
        Toast.makeText(context, "Initiating Orange Money payment for XAF " + amount + " to " + phoneNumber + "...", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (phoneNumber.length() > 5 && amount > 0) {
                String transactionId = "orange_money_" + UUID.randomUUID().toString();
                callback.onSuccess(transactionId, "Orange Money payment successful (simulated).");
            } else {
                callback.onError("Orange Money payment failed: Invalid phone number or amount.");
            }
        }, 3000);
    }

    /**
     * Placeholder for checking the status of a pending transaction.
     * In a real app, this would poll your backend, which in turn queries the payment provider.
     */
    public void checkPaymentStatus(String transactionId, PaymentCallback callback) {
        Toast.makeText(context, "Checking status for transaction: " + transactionId + "...", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (transactionId.contains("mtn_momo_") || transactionId.contains("stripe_txn_") && transactionId.endsWith("pending")) { // Simulate a previously pending Momo or Stripe payment now succeeding
                callback.onSuccess(transactionId, "Payment confirmed successfully.");
            } else if (transactionId.contains("failed_")) {
                callback.onError("Payment ultimately failed.");
            } else {
                // Simulate it's still pending or a generic success for others
                callback.onPending(transactionId, "Payment status is still pending or already completed.");
            }
        }, 1500);
    }
}
