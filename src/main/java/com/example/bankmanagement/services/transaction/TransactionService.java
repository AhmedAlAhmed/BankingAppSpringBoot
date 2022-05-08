package com.example.bankmanagement.services.transaction;

import com.example.bankmanagement.dto.constants.TransactionType;
import com.example.bankmanagement.dto.requests.transactions.DepositWithdrawRequest;
import com.example.bankmanagement.dto.requests.transactions.TransferRequest;
import com.example.bankmanagement.dto.responses.transactions.TransactionInfoResponse;
import com.example.bankmanagement.entities.Account;
import com.example.bankmanagement.entities.Transaction;
import com.example.bankmanagement.exceptions.AccountNotFoundException;
import com.example.bankmanagement.exceptions.InsufficientBalanceException;
import com.example.bankmanagement.repositories.AccountRepository;
import com.example.bankmanagement.repositories.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class TransactionService implements ITransactionService {

    final static String DEFAULT_CURRENCY = "AED";

    final TransactionRepository transactionRepository;

    final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * This function simulate deposit cash into the bank center
     * so, just read variables from the request and process them.
     * <p>
     * NOTE: this function support depositing into any account by
     * specifying the accountID, we use the ID field as bank_account_number
     * to make the project simple as possible as.
     */
    @CacheEvict(value="balances",  allEntries = true)
    @Transactional
    @Override
    public TransactionInfoResponse deposit(DepositWithdrawRequest request) {
        TransactionInfoResponse response = new TransactionInfoResponse();

        Optional<Account> destinationAccountResult = accountRepository.findById(request.getAccountId());
        if (destinationAccountResult.isEmpty()) {
            throw new AccountNotFoundException("Destination account is not found!");
        }


        Account destinationAccount = destinationAccountResult.get();

        // create transaction entry.
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setSourceAccount(null);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setCurrency(DEFAULT_CURRENCY);
        transaction.setFee(0.0); // zero fees for internal transactions.
        transaction.setTransactionType(TransactionType.DEPOSIT);

        Transaction createdTransaction = transactionRepository.save(transaction);

        // update the destination account balance.
        double newSourceAccountBalance = destinationAccount.getCurrentBalance() + request.getAmount();
        destinationAccount.setCurrentBalance(newSourceAccountBalance);

        accountRepository.save(destinationAccount);

        BeanUtils.copyProperties(createdTransaction, response);
        response.setAccountName(
                String.format("%s %s", destinationAccount.getFirstName(), destinationAccount.getLastName())
        );

        return response;

    }

    @CacheEvict(value="balances",  allEntries = true)
    @Transactional
    @Override
    public TransactionInfoResponse withdraw(DepositWithdrawRequest request) {
        TransactionInfoResponse response = new TransactionInfoResponse();
        Optional<Account> sourceAccountOptional = accountRepository.findById(request.getAccountId());
        if (sourceAccountOptional.isEmpty()) {
            throw new AccountNotFoundException("Source account is not found");
        }

        Account sourceAccount = sourceAccountOptional.get();

        // zero fees on internal transactions.
        if (sourceAccount.getCurrentBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("You do not have enough balance to do this operation.");
        }

        Transaction transaction = new Transaction();

        // make the transaction entry
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(DEFAULT_CURRENCY);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(null);
        transaction.setFee(0.0);

        Transaction createdTransaction = transactionRepository.save(transaction);

        // update user current balance.
        double newSourceAccountBalance = sourceAccount.getCurrentBalance() - request.getAmount();
        sourceAccount.setCurrentBalance(newSourceAccountBalance);
        accountRepository.save(sourceAccount);

        BeanUtils.copyProperties(createdTransaction, response);
        response.setAccountName(
                String.format("%s %s", sourceAccount.getFirstName(), sourceAccount.getLastName())
        );

        return response;

    }

    @CacheEvict(value="balances",  allEntries = true)
    @Transactional
    @Override
    public TransactionInfoResponse transfer(TransferRequest request) {
        TransactionInfoResponse response = new TransactionInfoResponse();

        Optional<Account> sourceAccountOptional = accountRepository.findById(request.getSourceAccountId());
        Optional<Account> destinationAccountOptional = accountRepository.findById(request.getDestinationAccountId());

        if (sourceAccountOptional.isEmpty() || destinationAccountOptional.isEmpty()) {
            throw new AccountNotFoundException("Source or destination account is not found.");
        }

        Account sourceAccount = sourceAccountOptional.get();
        Account destinationAccount = destinationAccountOptional.get();
        // send money from source account to destination account.

        if (sourceAccount.getCurrentBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("You do not have enough balance to do this operation.");
        }

        // each transfer transaction consists of 2 transactions:
        // create withdraw transaction from source to destination.
        // create deposit transaction from source to destination.

        Transaction sourceWithdrawTransaction = new Transaction();
        sourceWithdrawTransaction.setSourceAccount(sourceAccount);
        sourceWithdrawTransaction.setDestinationAccount(destinationAccount);
        sourceWithdrawTransaction.setCurrency(DEFAULT_CURRENCY);
        sourceWithdrawTransaction.setFee(0.0);
        sourceWithdrawTransaction.setAmount(request.getAmount());
        sourceWithdrawTransaction.setTransactionType(TransactionType.WITHDRAW);

        transactionRepository.save(sourceWithdrawTransaction);

        Transaction destinationDepositTransaction = new Transaction();
        destinationDepositTransaction.setSourceAccount(sourceAccount);
        destinationDepositTransaction.setDestinationAccount(destinationAccount);
        destinationDepositTransaction.setCurrency(DEFAULT_CURRENCY);
        destinationDepositTransaction.setFee(0.0);
        destinationDepositTransaction.setAmount(request.getAmount());
        destinationDepositTransaction.setTransactionType(TransactionType.DEPOSIT);

        transactionRepository.save(destinationDepositTransaction);

        // update balances, zero fees.
        double newSourceAccountBalance = sourceAccount.getCurrentBalance() - request.getAmount();
        double newDestinationAccountBalance = destinationAccount.getCurrentBalance() + request.getAmount();

        sourceAccount.setCurrentBalance(newSourceAccountBalance);
        destinationAccount.setCurrentBalance(newDestinationAccountBalance);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        BeanUtils.copyProperties(destinationDepositTransaction, response);
        response.setAccountName(String.format("%s %s", destinationAccount.getFirstName(), destinationAccount.getLastName()));
        return response;
    }
}
