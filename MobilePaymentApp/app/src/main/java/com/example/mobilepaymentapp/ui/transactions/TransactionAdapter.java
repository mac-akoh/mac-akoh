package com.example.mobilepaymentapp.ui.transactions;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.model.Transaction;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.textViewTransactionDescription.setText(transaction.getDescription());
        holder.textViewTransactionDate.setText(dateFormat.format(transaction.getDate()));

        // Set amount and color based on type
        String amountString;
        String currencySymbol = "XAF"; // Use XAF currency symbol

        if ("DEBIT".equalsIgnoreCase(transaction.getType()) || "PAYMENT".equalsIgnoreCase(transaction.getType()) || "TRANSFER_OUT".equalsIgnoreCase(transaction.getType())) {
            amountString = String.format(Locale.getDefault(), "-%.2f %s", transaction.getAmount(), currencySymbol);
            holder.textViewTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            // Potentially set a specific icon for debit/payment
            // holder.imageViewTransactionType.setImageResource(R.drawable.ic_payment_sent);
        } else if ("CREDIT".equalsIgnoreCase(transaction.getType()) || "TRANSFER_IN".equalsIgnoreCase(transaction.getType())) {
            amountString = String.format(Locale.getDefault(), "+%.2f %s", transaction.getAmount(), currencySymbol);
            holder.textViewTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            // Potentially set a specific icon for credit
            // holder.imageViewTransactionType.setImageResource(R.drawable.ic_payment_received);
        } else {
            amountString = String.format(Locale.getDefault(), "%.2f %s", transaction.getAmount(), currencySymbol);
            holder.textViewTransactionAmount.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
        holder.textViewTransactionAmount.setText(amountString);

        holder.textViewTransactionStatus.setText(transaction.getStatus());
        // Set status color
        switch (transaction.getStatus().toUpperCase()) {
            case "COMPLETED":
                holder.textViewTransactionStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                break;
            case "PENDING":
                holder.textViewTransactionStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
                break;
            case "FAILED":
                holder.textViewTransactionStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                break;
            default:
                holder.textViewTransactionStatus.setTextColor(ContextCompat.getColor(context, R.color.black)); // Or a default color
                break;
        }

        // Placeholder for icon - ideally, select icon based on transaction.getType()
        holder.imageViewTransactionType.setImageResource(R.drawable.ic_payment); // Default icon
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewTransactionType;
        TextView textViewTransactionDescription;
        TextView textViewTransactionDate;
        TextView textViewTransactionAmount;
        TextView textViewTransactionStatus;

        TransactionViewHolder(View view) {
            super(view);
            imageViewTransactionType = view.findViewById(R.id.imageViewTransactionType);
            textViewTransactionDescription = view.findViewById(R.id.textViewTransactionDescription);
            textViewTransactionDate = view.findViewById(R.id.textViewTransactionDate);
            textViewTransactionAmount = view.findViewById(R.id.textViewTransactionAmount);
            textViewTransactionStatus = view.findViewById(R.id.textViewTransactionStatus);
        }
    }

    // Helper method to update data if needed
    public void updateData(List<Transaction> newTransactions) {
        this.transactionList.clear();
        this.transactionList.addAll(newTransactions);
        notifyDataSetChanged();
    }
}
