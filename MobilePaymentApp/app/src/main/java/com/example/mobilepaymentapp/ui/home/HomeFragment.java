package com.example.mobilepaymentapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilepaymentapp.R;
import com.example.mobilepaymentapp.ui.transactions.TransactionHistoryActivity;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    private TextView textViewBalanceAmount;
    private MaterialButton buttonSendMoney;
    private MaterialButton buttonRequestMoney;
    private TextView textViewViewAllHistory;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewBalanceAmount = view.findViewById(R.id.textViewBalanceAmount);
        buttonSendMoney = view.findViewById(R.id.buttonSendMoney);
        buttonRequestMoney = view.findViewById(R.id.buttonRequestMoney);
        textViewViewAllHistory = view.findViewById(R.id.textViewViewAllHistory);

        // Set placeholder data or load actual data
        textViewBalanceAmount.setText("1,234.56 XAF"); // Placeholder

        buttonSendMoney.setOnClickListener(v -> {
            // Placeholder action
            Toast.makeText(getContext(), "Send Money Clicked (Placeholder)", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Send Money screen or show dialog
        });

        buttonRequestMoney.setOnClickListener(v -> {
            // Placeholder action
            Toast.makeText(getContext(), "Request Money Clicked (Placeholder)", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Request Money screen or show dialog
        });

        textViewViewAllHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
            startActivity(intent);
        });
    }
}
