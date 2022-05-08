package com.example.bankmanagement.services.stripe;

import com.example.bankmanagement.dto.requests.stripe.CreateStripeCustomerRequest;
import com.example.bankmanagement.dto.requests.stripe.StripeChargeRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import lombok.NoArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
@NoArgsConstructor
public class StripeService {

    public Charge createCharge(StripeChargeRequest chargeRequest) throws StripeException {

        String sourceCard = Customer.retrieve(chargeRequest.getCustomerId()).getDefaultSource();
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("customer", chargeRequest.getCustomerId());
        chargeParams.put("source", sourceCard);
        return Charge.create(chargeParams);
    }

    public Customer createCustomer(CreateStripeCustomerRequest request) throws StripeException {
        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", request.getEmail());
        customerParams.put("source", request.getToken());
        return Customer.create(customerParams);
    }
}
