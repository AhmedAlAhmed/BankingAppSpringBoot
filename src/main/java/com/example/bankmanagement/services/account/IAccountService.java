package com.example.bankmanagement.services.account;

import com.example.bankmanagement.dto.requests.accounts.CreateAccountRequest;
import com.example.bankmanagement.dto.responses.accounts.AccountBalanceResponse;
import com.example.bankmanagement.entities.Account;

import java.util.List;

public interface IAccountService {
    Account createAccount(CreateAccountRequest request);

    List<Account> getAccountsList(int pageNo, int pageSize, String sortBy);

    void deleteAccount(long accountId);

    AccountBalanceResponse getCurrentBalance(long accountId);
}
