package com.example.bankmanagement.services.account;

import com.example.bankmanagement.dto.constants.TransactionType;
import com.example.bankmanagement.dto.requests.accounts.CreateAccountRequest;
import com.example.bankmanagement.dto.responses.accounts.AccountBalanceResponse;
import com.example.bankmanagement.entities.Account;
import com.example.bankmanagement.entities.Transaction;
import com.example.bankmanagement.exceptions.AccountNotFoundException;
import com.example.bankmanagement.repositories.AccountRepository;
import com.example.bankmanagement.repositories.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AccountService implements IAccountService {

    final AccountRepository accountRepository;
    final TransactionRepository transactionRepository;

    // Preferred over @Autowired.
    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account();
        BeanUtils.copyProperties(request, account);
        return this.accountRepository.save(account);
    }

    @Override
    public List<Account> getAccountsList(int pageNo, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<Account> accountsPage = accountRepository.findAll(paging);
        if (accountsPage.hasContent()) {
            return accountsPage.getContent();
        } else {
            return new ArrayList<>();
        }
    }


    @Override
    public void deleteAccount(long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(String.format(
                    "Account #%s is not found.", accountId
            ));
        }

        accountRepository.deleteById(accountId);
    }

    @Cacheable("balances")
    @Override
    public AccountBalanceResponse getCurrentBalance(long accountId) {
        AccountBalanceResponse response = new AccountBalanceResponse();

        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException("Account is not found");
        }

        Account account = accountOptional.get();

        // TODO Calculate the balance according to all his transactions
        // TODO Cache the result to avoid requesting it everytime, remove the cache after each transaction.
        response.setCurrentBalance(account.getCurrentBalance());
        return response;
    }
}
