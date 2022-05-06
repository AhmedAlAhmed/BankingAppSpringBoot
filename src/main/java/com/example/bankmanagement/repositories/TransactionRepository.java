package com.example.bankmanagement.repositories;

import com.example.bankmanagement.entities.Account;
import com.example.bankmanagement.entities.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findAllByDestinationAccount(Account destinationAccount);
}
