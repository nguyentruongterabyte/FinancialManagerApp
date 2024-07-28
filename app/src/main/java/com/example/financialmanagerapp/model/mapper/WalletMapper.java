package com.example.financialmanagerapp.model.mapper;

import com.example.financialmanagerapp.model.Wallet;
import com.example.financialmanagerapp.model.DTO.WalletDTO;

public class WalletMapper {
    public static WalletDTO toWalletDTO(Wallet wallet) {
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.set_name(wallet.get_name());
        walletDTO.set_initial_amount(wallet.get_initial_amount());
        walletDTO.set_amount(wallet.get_amount());
        walletDTO.set_color(wallet.get_color());
        walletDTO.set_exclude(wallet.get_exclude());
        walletDTO.set_account_id(wallet.get_account_id());
        walletDTO.set_wallet_type_code(wallet.get_wallet_type_code());
        walletDTO.set_icon(wallet.get_icon());
        return walletDTO;
    }
}
