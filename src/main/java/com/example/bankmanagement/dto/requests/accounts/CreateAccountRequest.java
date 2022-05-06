package com.example.bankmanagement.dto.requests.accounts;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {
    private String firstName;
    private String lastName;
    private String email;
}
