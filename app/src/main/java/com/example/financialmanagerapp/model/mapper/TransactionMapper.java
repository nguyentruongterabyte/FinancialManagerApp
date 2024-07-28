package com.example.financialmanagerapp.model.mapper;

import com.example.financialmanagerapp.model.Transaction;
import com.example.financialmanagerapp.model.DTO.TransactionDTO;

public class TransactionMapper {
    public static TransactionDTO toTransactionDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.set_amount(transaction.get_amount());
        transactionDTO.set_description(transaction.get_description());
        transactionDTO.set_wallet_id(transaction.get_wallet_id());
        transactionDTO.set_category_id(transaction.get_category_id());
        transactionDTO.set_memo(transaction.get_memo());
        transactionDTO.set_from_wallet_id(transaction.get_from_wallet_id());
        transactionDTO.set_to_wallet_id(transaction.get_to_wallet_id());
        transactionDTO.set_fee(transaction.get_fee());
        transactionDTO.set_transaction_type_id(transaction.get_transaction_type_id());
        transactionDTO.set_date(transaction.get_date());

        return transactionDTO;
    }
}
