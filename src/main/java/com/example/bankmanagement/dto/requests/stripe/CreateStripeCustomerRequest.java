package com.example.bankmanagement.dto.requests.stripe;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStripeCustomerRequest {
    private String email;
    protected String token;
}
