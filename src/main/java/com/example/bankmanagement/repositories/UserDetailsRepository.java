package com.example.bankmanagement.repositories;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsRepository {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
