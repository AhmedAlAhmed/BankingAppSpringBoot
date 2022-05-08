package com.example.bankmanagement.services.transaction;

import com.example.bankmanagement.dto.constants.StripeConstants;
import com.example.bankmanagement.dto.constants.TransactionType;
import com.example.bankmanagement.dto.requests.stripe.StripeChargeRequest;
import com.example.bankmanagement.dto.requests.transactions.DepositWithdrawRequest;
import com.example.bankmanagement.dto.requests.transactions.TransferRequest;
import com.example.bankmanagement.dto.responses.transactions.TransactionInfoResponse;
import com.example.bankmanagement.entities.Account;
import com.example.bankmanagement.entities.Transaction;
import com.example.bankmanagement.exceptions.AccountNotFoundException;
import com.example.bankmanagement.exceptions.InsufficientBalanceException;
import com.example.bankmanagement.repositories.AccountRepository;
import com.example.bankmanagement.repositories.TransactionRepository;
import com.example.bankmanagement.services.stripe.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
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
    final StripeService stripeService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              StripeService stripeService
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.stripeService = stripeService;
    }

    /**
     * This function simulate deposit cash into the bank center
     * so, just read variables from the request and process them.
     * <p>
     * NOTE: this function support depositing into any account by
     * specifying the accountID, we use the ID field as bank_account_number
     * to make the project simple as possible as.
     */
    @CacheEvict(value = "balances", allEntries = true)
    @Transactional
    @Override
    public TransactionInfoResponse deposit(DepositWithdrawRequest request) {
        TransactionInfoResponse response = new TransactionInfoResponse();


        Account destinationAccount = getAccountFromDepositWithdrawRequest(request);

        // create transaction entry.
        Transaction createdTransaction = createDepositTransaction(null, destinationAccount, request.getAmount(), 0);

        // update the destination account balance.
        updateAccountBalance(destinationAccount, request.getAmount());

        BeanUtils.copyProperties(createdTransaction, response);
        response.setAccountName(
                String.format("%s %s", destinationAccount.getFirstName(), destinationAccount.getLastName())
        );

        return response;

    }

    @CacheEvict(value = "balances", allEntries = true)
    @Transactional
    @Override
    public TransactionInfoResponse withdraw(DepositWithdrawRequest request) {
        TransactionInfoResponse response = new TransactionInfoResponse();

        Account sourceAccount = getAccountFromDepositWithdrawRequest(request);

        // zero fees on internal transactions.
        if (!sourceAccount.canTransfer(request.getAmount())) {
            throw new InsufficientBalanceException("You do not have enough balance to do this operation.");
        }

        Transaction createdTransaction = createWithdrawTransaction(sourceAccount, null, request.getAmount(), 0);

        // update user current balance.
        updateAccountBalance(sourceAccount, -1 * request.getAmount());

        BeanUtils.copyProperties(createdTransaction, response);
        response.setAccountName(
                String.format("%s %s", sourceAccount.getFirstName(), sourceAccount.getLastName())
        );

        return response;

    }

    @CacheEvict(value = "balances", allEntries = true)
    @Transactional
    @Override
    public TransactionInfoResponse transfer(TransferRequest request) {
        TransactionInfoResponse response = new TransactionInfoResponse();

        // Extract accounts & doing validation.
        Account[] accounts = getAccountsFromTransferRequest(request);

        Account sourceAccount = accounts[0];
        Account destinationAccount = accounts[1];

        // send money from source account to destination account.

        if (!sourceAccount.canTransfer(request.getAmount())) {
            throw new InsufficientBalanceException("You do not have enough balance to do this operation.");
        }

        // each transfer transaction consists of 2 transactions:
        // create withdraw transaction from source to destination.
        // create deposit transaction from source to destination.

        createWithdrawTransaction(sourceAccount, destinationAccount, request.getAmount(), 0);
        Transaction destinationDepositTransaction = createDepositTransaction(sourceAccount, destinationAccount, request.getAmount(), 0);

        // update balances, zero fees.

        updateAccountBalance(sourceAccount, -1 * request.getAmount());
        updateAccountBalance(destinationAccount, request.getAmount());


        BeanUtils.copyProperties(destinationDepositTransaction, response);
        response.setAccountName(String.format("%s %s", destinationAccount.getFirstName(), destinationAccount.getLastName()));
        return response;
    }

    @CacheEvict(value = "balances", allEntries = true)
    @Transactional
    @Override
    public TransactionInfoResponse externalTransfer(TransferRequest request) throws StripeException {
        TransactionInfoResponse response = new TransactionInfoResponse();
        // Extract accounts & doing validation.
        Account[] accounts = getAccountsFromTransferRequest(request);

        Account sourceAccount = accounts[0];
        Account destinationAccount = accounts[1];

        // send amount from current user card to bank stripe account.
        StripeChargeRequest stripeChargeRequest = new StripeChargeRequest(
                (int) request.getAmount(),
                sourceAccount.getStripeCustomerId(),
                DEFAULT_CURRENCY
        );

        Charge charge = stripeService.createCharge(stripeChargeRequest);


        // the best way to handle stripe payment is using webhook, but it will require real-server to hit it once the action triggered.
        // to make code simple as possible as, I have just waited the response from stripe server
        // and proceed according to its response.
        if (!charge.getStatus().equalsIgnoreCase(StripeConstants.STRIPE_CHARGE_STATUS_SUCCEEDED)) {
            throw new RuntimeException("Something wrong with stripe payment.");
        }

        // each transfer transaction consists of 2 transactions:
        // create withdraw transaction from source to destination.
        // create deposit transaction from source to destination.

        double chargeFee = 0.0;
        double chargedAmount = request.getAmount();

        if (charge.getAmount() != null) {
            chargedAmount = charge.getAmount();
        }

        if (charge.getApplicationFeeAmount() != null) {
            chargeFee = charge.getApplicationFeeAmount();
        }


        Transaction sourceWithdrawTransaction = createWithdrawTransaction(sourceAccount, destinationAccount, chargedAmount, chargeFee);
        Transaction destinationDepositTransaction = createDepositTransaction(sourceAccount, destinationAccount, request.getAmount(), 0);

        // update balances, zero fees.

        updateAccountBalance(destinationAccount, request.getAmount());

        BeanUtils.copyProperties(destinationDepositTransaction, response);
        response.setAccountName(String.format("%s %s", destinationAccount.getFirstName(), destinationAccount.getLastName()));
        return response;
    }

    protected Account getAccountFromDepositWithdrawRequest(DepositWithdrawRequest request) {
        Optional<Account> account = accountRepository.findById(request.getAccountId());
        if (account.isEmpty()) {
            throw new AccountNotFoundException("Source account is not found");
        }

        return account.get();
    }

    protected Account[] getAccountsFromTransferRequest(TransferRequest request) {
        Account[] accounts = new Account[2];
        Optional<Account> sourceAccountOptional = accountRepository.findById(request.getSourceAccountId());
        Optional<Account> destinationAccountOptional = accountRepository.findById(request.getDestinationAccountId());

        if (sourceAccountOptional.isEmpty() || destinationAccountOptional.isEmpty()) {
            throw new AccountNotFoundException("Source or destination account is not found.");
        }

        accounts[0] = sourceAccountOptional.get();
        accounts[1] = destinationAccountOptional.get();

        return accounts;
    }

    protected void updateAccountBalance(Account account, double amount) {
        account.setCurrentBalance(account.getCurrentBalance() + amount);
        accountRepository.save(account);
    }

    protected Transaction createDepositTransaction(Account source, Account destination, double amount, double fee) {
        return createTransaction(source, destination, amount, fee, TransactionType.DEPOSIT);
    }

    protected Transaction createWithdrawTransaction(Account source, Account destination, double amount, double fee) {
        return createTransaction(source, destination, amount, fee, TransactionType.WITHDRAW);
    }

    protected Transaction createTransaction(Account source, Account destination, double amount, double fee, String transactionType) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(source);
        transaction.setDestinationAccount(destination);
        transaction.setCurrency(DEFAULT_CURRENCY);
        transaction.setFee(fee);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);


        return transactionRepository.save(transaction);
    }
}
