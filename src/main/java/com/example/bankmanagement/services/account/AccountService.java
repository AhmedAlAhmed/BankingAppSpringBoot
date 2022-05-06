package com.example.bankmanagement.services.account;

import com.example.bankmanagement.dto.requests.accounts.CreateAccountRequest;
import com.example.bankmanagement.entities.Account;
import com.example.bankmanagement.exceptions.AccountNotFoundException;
import com.example.bankmanagement.repositories.AccountRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements IAccountService {

    final AccountRepository accountRepository;

    // Preferred over @Autowired.
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
}
