package com.example.financialmanagerapp.activity.fragment.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.adapter.WalletAdapter;
import com.example.financialmanagerapp.model.SharedViewModel;
import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.retrofit.FinancialManagerAPI;
import com.example.financialmanagerapp.retrofit.RetrofitClient;
import com.example.financialmanagerapp.utils.Utils;

import retrofit2.Retrofit;

public class SelectingWalletFragment extends DialogFragment {
    private ImageButton btnBack;
    private ListView listView;
    protected LinearLayout doneContainer;
    protected FinancialManagerAPI apiService;
    protected SharedViewModel sharedViewModel;
    protected int walletType;

    public SelectingWalletFragment(int walletType) {
        this.walletType = walletType;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selecting_wallet, container, false);
        setControl(view);
        initData();
        setEvents();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Make the dialog full screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
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
    private void setEvents() {
        // handle button back clicked
        btnBack.setOnClickListener(v -> dismiss());

        // handle list item clicked
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Wallet selectedWallet = Utils.currentUser.getWallets().get(position);
            switch (walletType) {
                case Utils.TO_WALLET_TYPE:
                    sharedViewModel.setWallet(selectedWallet);
                    break;
                case Utils.FROM_WALLET_TYPE:
                    sharedViewModel.setFromWallet(selectedWallet);
                    break;
                default:
                    break;
            }


            // Dismiss the fragment
            dismiss();
        });
    }

    private void initData() {
        Retrofit retrofit = RetrofitClient.getInstance(Utils.BASE_URL, requireContext());
        apiService = retrofit.create(FinancialManagerAPI.class);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if (Utils.currentUser.getWallets().size() != 0)
            initWallets();
    }

    private void initWallets() {
        WalletAdapter adapter = new WalletAdapter(requireContext(), Utils.currentUser.getWallets(),
                Utils.walletIcons, false, null, false);
        listView.setAdapter(adapter);
    }

    private void setControl(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        listView = view.findViewById(R.id.list_view);

        doneContainer = view.findViewById(R.id.done_container);
        doneContainer.setVisibility(View.GONE);
    }
}
