package com.petcarehub.service;

import com.petcarehub.exception.AuthenticationException;
import com.petcarehub.model.*;
import com.petcarehub.repository.UserRepository;
import com.petcarehub.util.AuditLog;
import com.petcarehub.util.PasswordUtil;
import com.petcarehub.util.Validation;

import java.io.IOException;
import java.util.List;

public final class AuthService {
    private final UserRepository users;
    private final AuditLog audit;

    public AuthService(UserRepository users, AuditLog audit) {
        this.users = users;
        this.audit = audit;
    }

    public Person login(String userId, String password) {
        try {
            Person person = users.findById(Validation.required(userId, "User ID"))
                    .orElseThrow(() -> new AuthenticationException("User not found."));
            if (!PasswordUtil.verify(password, person.getPasswordHash())) {
                throw new AuthenticationException("Invalid password.");
            }
            // Seamlessly migrate legacy plain-text passwords on successful login.
            if (!PasswordUtil.isHashed(person.getPasswordHash())) {
                List<Person> all = users.findAll();
                all.stream().filter(p -> p.getUserId().equalsIgnoreCase(person.getUserId()))
                        .findFirst().ifPresent(p -> p.setPasswordHash(PasswordUtil.hash(password)));
                users.saveAll(all);
            }
            audit.record(person.getUserId(), "LOGIN");
            return person;
        } catch (IOException ex) {
            throw new AuthenticationException("Unable to read user data: " + ex.getMessage());
        }
    }

    public Person register(String userId, String firstName, String lastName, Gender gender,
                           String password, Role role) {
        Validation.required(userId, "User ID");
        Validation.required(firstName, "First name");
        Validation.required(lastName, "Last name");
        Validation.strongEnoughPassword(password);
        if (role == Role.ADMIN) throw new IllegalArgumentException("Admin accounts cannot be self-registered.");
        try {
            List<Person> all = users.findAll();
            if (all.stream().anyMatch(p -> p.getUserId().equalsIgnoreCase(userId.trim()))) {
                throw new IllegalArgumentException("User ID already exists.");
            }
            String hash = PasswordUtil.hash(password);
            Person person = role == Role.HELPER
                    ? new Helper(userId.trim(), firstName.trim(), lastName.trim(), gender, hash, 5.0, false, "")
                    : new User(userId.trim(), firstName.trim(), lastName.trim(), gender, hash);
            all.add(person);
            users.saveAll(all);
            audit.record(person.getUserId(), "REGISTER role=" + role);
            return person;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save registration: " + ex.getMessage(), ex);
        }
    }
}
