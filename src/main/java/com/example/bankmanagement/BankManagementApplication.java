package com.example.bankmanagement;

import com.example.bankmanagement.entities.User;
import com.example.bankmanagement.repositories.UserRepository;
import com.stripe.Stripe;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

@OpenAPIDefinition(info = @Info(title = "Banking App API", version = "1.0", description = "Simple Banking Application - Assignment"))
@SecurityScheme(name = "bankingapp", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@SpringBootApplication
public class BankManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankManagementApplication.class, args);
    }

    public BankManagementApplication(Environment environment, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        Stripe.apiKey = environment.getProperty("stripe.secret.key");

        // Seeding database with an example of employee records.
        if (userRepository.count() == 0) {
            User defaultEmployee = new User();
            defaultEmployee.setEmail("admin@admin.com");
            defaultEmployee.setPassword(passwordEncoder.encode("12345678"));

            userRepository.save(defaultEmployee);
        }
    }

}
