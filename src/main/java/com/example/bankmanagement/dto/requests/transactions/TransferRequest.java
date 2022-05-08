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
public class TransferRequest {

    @NotNull
    private long sourceAccountId;

    @NotNull
    private long destinationAccountId;

    @NotNull
    @Min(500)
    @Max(10000)
    private double amount;
}
