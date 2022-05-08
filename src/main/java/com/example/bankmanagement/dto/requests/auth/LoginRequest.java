package com.example.bankmanagement.dto.requests.auth;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;
}
