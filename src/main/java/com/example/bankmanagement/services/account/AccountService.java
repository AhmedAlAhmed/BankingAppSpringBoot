package com.example.bankmanagement.services.account;

import com.example.bankmanagement.dto.requests.accounts.CreateAccountRequest;
import com.example.bankmanagement.dto.requests.stripe.CreateStripeCustomerRequest;
import com.example.bankmanagement.dto.responses.accounts.AccountBalanceResponse;
import com.example.bankmanagement.entities.Account;
import com.example.bankmanagement.exceptions.AccountNotFoundException;
import com.example.bankmanagement.repositories.AccountRepository;
import com.example.bankmanagement.repositories.TransactionRepository;
import com.example.bankmanagement.services.stripe.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.logging.Logger;

@Service
public class AccountService implements IAccountService {

    final AccountRepository accountRepository;
    final TransactionRepository transactionRepository;

    final StripeService stripeService;

    // Preferred over @Autowired.
    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          StripeService stripeService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.stripeService = stripeService;
    }

    @Override
    public Account createAccount(CreateAccountRequest request) throws StripeException {
        Account account = new Account();
        BeanUtils.copyProperties(request, account);

        if (request.getToken() != null) {
            CreateStripeCustomerRequest customerRequest = new CreateStripeCustomerRequest();

            customerRequest.setEmail(request.getEmail());
            customerRequest.setToken(request.getToken());

            Customer stripeCustomer = this.stripeService.createCustomer(customerRequest);
            if (stripeCustomer != null) {
                account.setStripeCustomerId(stripeCustomer.getId());
            }
        }

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

    @Cacheable(value = "balances", key = "#accountId")
    @Override
    public double getCurrentBalance(long accountId) {

        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException("Account is not found");
        }

        Account account = accountOptional.get();

        // Because we update the current balance on each transaction operation
        // we do not need to ho through all the transactions to calculate the current user balance.
        // this way saves times & resources.

        return account.getCurrentBalance();
    }
}
