package com.example.bankmanagement.controllers;

import com.example.bankmanagement.dto.requests.transactions.DepositWithdrawRequest;
import com.example.bankmanagement.dto.responses.transactions.TransactionInfoResponse;
import com.example.bankmanagement.entities.Transaction;
import com.example.bankmanagement.services.transaction.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/transactions")
public class TransactionController {

    final ITransactionService transactionService;

    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Allow account to deposit some cash into his or any account by account ID (bank account number)")
    @PostMapping(value = "/deposit")
    public ResponseEntity<TransactionInfoResponse> deposit(@RequestBody DepositWithdrawRequest request) {
        TransactionInfoResponse response = transactionService.deposit(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Allow account wo withdraw some cash from his account.")
    @PostMapping(value = "/withdraw")
    public ResponseEntity<TransactionInfoResponse> withdraw(@RequestBody DepositWithdrawRequest request) {
        TransactionInfoResponse response = transactionService.withdraw(request);
        return ResponseEntity.ok(response);
    }
}