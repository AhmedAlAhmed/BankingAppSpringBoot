package com.example.bankmanagement.services.transaction;

import com.example.bankmanagement.dto.requests.transactions.DepositWithdrawRequest;
import com.example.bankmanagement.dto.requests.transactions.TransferRequest;
import com.example.bankmanagement.dto.responses.transactions.TransactionInfoResponse;
import com.example.bankmanagement.entities.Transaction;

public interface ITransactionService {
    TransactionInfoResponse deposit(DepositWithdrawRequest request);

    TransactionInfoResponse withdraw(DepositWithdrawRequest request);

    TransactionInfoResponse transfer(TransferRequest request);
}
