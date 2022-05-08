package com.example.bankmanagement.dto.requests.transactions;


import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepositWithdrawRequest {

    @NotNull
    @Min(1)
    @Max(10000)
    private double amount;

    @NotNull
    private Long accountId;
}
