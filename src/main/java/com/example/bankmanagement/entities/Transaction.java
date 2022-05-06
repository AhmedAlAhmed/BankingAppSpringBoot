package com.example.bankmanagement.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "source_account_id", nullable = true)
    private Account sourceAccount;

    @OneToOne
    @JoinColumn(name = "destination_account_id", nullable = true)
    private Account destinationAccount;

    private String currency;

    private double amount;

    private double fee;

    private String transactionType;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}


