package com.example.bankmanagement.dto.requests.stripe;


import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StripeChargeRequest {
    private int amount;
    private String customerId;
    private String currency;
}
