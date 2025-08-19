package com.example.mobilepaymentapp.ui.transactions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.model.Transaction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private TextView textViewNoTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Transaction History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
        }

        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);
        textViewNoTransactions = findViewById(R.id.textViewNoTransactions);

        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(this, transactionList);
        recyclerViewTransactions.setAdapter(transactionAdapter);

        loadPlaceholderTransactions();
    }

    private void loadPlaceholderTransactions() {
        // In a real app, this data would come from a database or API
        transactionList.clear(); // Clear existing before adding new ones

        Calendar cal = Calendar.getInstance();

        cal.set(2023, Calendar.OCTOBER, 15, 10, 30);
        transactionList.add(new Transaction("txn1001", "Payment to Coffee Shop", 5.75, cal.getTime(), "PAYMENT", "COMPLETED", "The Daily Grind"));

        cal.set(2023, Calendar.OCTOBER, 14, 15, 20);
        transactionList.add(new Transaction("txn1002", "Money Received from Alice", 50.00, cal.getTime(), "CREDIT", "COMPLETED", "Alice Wonderland"));

        cal.set(2023, Calendar.OCTOBER, 14, 9, 05);
        transactionList.add(new Transaction("txn1003", "Online Subscription", 12.99, cal.getTime(), "DEBIT", "COMPLETED", "MusicStream Inc."));

        cal.set(2023, Calendar.OCTOBER, 13, 18, 45);
        transactionList.add(new Transaction("txn1004", "Transfer to Savings", 200.00, cal.getTime(), "TRANSFER_OUT", "PENDING", "My Savings Account"));

        cal.set(2023, Calendar.OCTOBER, 12, 11, 10);
        transactionList.add(new Transaction("txn1005", "Grocery Store Purchase", 75.50, cal.getTime(), "PAYMENT", "FAILED", "SuperMart"));

        cal.set(2023, Calendar.SEPTEMBER, 28, 17,00);
        transactionList.add(new Transaction("txn1006", "Refund from Amazon", 25.00, cal.getTime(), "CREDIT", "COMPLETED", "Amazon"));


        if (transactionList.isEmpty()) {
            textViewNoTransactions.setVisibility(View.VISIBLE);
            recyclerViewTransactions.setVisibility(View.GONE);
        } else {
            textViewNoTransactions.setVisibility(View.GONE);
            recyclerViewTransactions.setVisibility(View.VISIBLE);
        }
        transactionAdapter.notifyDataSetChanged(); // Notify adapter after adding all items
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle the back button action
        return true;
    }
}
