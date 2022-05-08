package com.example.bankmanagement.services.account;

import com.example.bankmanagement.dto.requests.accounts.CreateAccountRequest;
import com.example.bankmanagement.dto.responses.accounts.AccountBalanceResponse;
import com.example.bankmanagement.entities.Account;
import com.stripe.exception.StripeException;

import java.util.List;

public interface IAccountService {
    Account createAccount(CreateAccountRequest request) throws StripeException;

    List<Account> getAccountsList(int pageNo, int pageSize, String sortBy);

    void deleteAccount(long accountId);

    double getCurrentBalance(long accountId);
}
