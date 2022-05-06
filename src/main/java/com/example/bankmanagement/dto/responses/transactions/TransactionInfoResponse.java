package com.example.bankmanagement.dto.responses.transactions;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInfoResponse {
    private Long id;
    private String accountName;
    private double amount;
    private double fee;
    private LocalDateTime createdAt;
    private String transactionType;
}
