package com.example.bankmanagement.dto.requests.accounts;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {
    @NotNull
    @Size(min = 3, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 50)
    private String lastName;

    @NotNull
    @Email
    private String email;
}
