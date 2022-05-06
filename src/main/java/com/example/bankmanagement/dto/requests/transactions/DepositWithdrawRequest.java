package com.example.bankmanagement.dto.requests.transactions;


import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepositWithdrawRequest {
    private double amount;
    private Long accountId;
}
