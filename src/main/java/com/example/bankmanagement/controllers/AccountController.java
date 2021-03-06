package com.example.bankmanagement.controllers;

import com.example.bankmanagement.dto.requests.accounts.CreateAccountRequest;
import com.example.bankmanagement.dto.responses.accounts.AccountBalanceResponse;
import com.example.bankmanagement.dto.responses.common.BasicResponse;
import com.example.bankmanagement.entities.Account;
import com.example.bankmanagement.services.account.IAccountService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@SecurityRequirement(name = "bankingapp")
@RestController
@RequestMapping(value = "/api/v1/accounts")
public class AccountController {
    final
    IAccountService accountService;

    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Create new bank account.")
    @PostMapping(value = "")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) throws StripeException {
        Account response = this.accountService.createAccount(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get list of all bank accounts")
    @GetMapping(value = "")
    public ResponseEntity<List<Account>> getAccountsList(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<Account> response = this.accountService.getAccountsList(pageNo, pageSize, sortBy);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete account by id.")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<BasicResponse> deleteAccount(@PathVariable Long id) {
        this.accountService.deleteAccount(id);
        return ResponseEntity.ok(new BasicResponse("Account has been deleted."));
    }

    @Operation(summary = "Return the last updated balance of your account")
    @GetMapping(value = "/{id}/balance")
    public ResponseEntity<AccountBalanceResponse> getCurrentBalance(@PathVariable Long id) {

        double currentBalance = this.accountService.getCurrentBalance(id);
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setCurrentBalance(currentBalance);
        return ResponseEntity.ok(response);
    }
}
