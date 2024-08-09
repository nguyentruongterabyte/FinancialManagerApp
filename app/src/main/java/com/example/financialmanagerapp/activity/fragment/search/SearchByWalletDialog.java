package com.example.financialmanagerapp.activity.fragment.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.WalletAdapter;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchByWalletDialog extends DialogFragment {
    private ImageButton btnBack;
    private ListView listView;
    private TextView btnDone;
    private WalletAdapter adapter;
    protected SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selecting_wallet, container, false);
        setControl(view);
        initData();
        setEvents();
        return view;
    }

    private void setEvents() {
        // handle button back clicked
        btnBack.setOnClickListener(v -> dismiss());

        // handle button done clicked
        btnDone.setOnClickListener(v -> {
            List<Integer> walletIds = new ArrayList<>();
            WalletAdapter adapter = (WalletAdapter) listView.getAdapter();
            Map<Integer, Boolean> checkBoxState = adapter.getCheckBoxStateMap();

            for (Map.Entry<Integer, Boolean> entry : checkBoxState.entrySet()) {
                int walletId = entry.getKey();
                boolean isChecked = entry.getValue();

                if (isChecked) {
                    walletIds.add(walletId);
                }
            }

            // set walletIds to live data
            sharedViewModel.setWalletIds(walletIds);

            sharedViewModel.setIsSetWalletsDone(true);
            dismiss();
        });

        // observe live data
        sharedViewModel.getWalletIds().observe(getViewLifecycleOwner(), walletIds -> {
            if (adapter == null) {
                adapter = new WalletAdapter(requireContext(), Utils.currentUser.getWallets(), Utils.walletIcons, true, walletIds);
                listView.setAdapter(adapter);
            } else {
                adapter.updateWallets(Utils.currentUser.getWallets(), walletIds);
            }
        });
    }

    private void initData() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        adapter = new WalletAdapter(requireContext(), Utils.currentUser.getWallets(), Utils.walletIcons, true, null);
        listView.setAdapter(adapter);
    }

    private void setControl(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnDone = view.findViewById(R.id.btn_done);

        listView = view.findViewById(R.id.list_view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Make the dialog full screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // set match parent to dialog
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // set match parent to dialog
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
