package com.example.bankmanagement.dto.requests.transactions;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    private long sourceAccountId;
    private long destinationAccountId;
    private double amount;
}
