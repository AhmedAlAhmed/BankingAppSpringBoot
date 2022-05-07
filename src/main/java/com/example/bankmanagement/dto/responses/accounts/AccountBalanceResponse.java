package com.example.bankmanagement.dto.responses.accounts;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceResponse {
    public double currentBalance;
}
