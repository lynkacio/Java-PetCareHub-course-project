package com.petcarehub.service;

import com.petcarehub.model.*;
import com.petcarehub.repository.UserRepository;

import java.io.IOException;
import java.util.List;

public final class AccountService {
    private final UserRepository users;

    public AccountService(UserRepository users) { this.users = users; }

    public List<Person> all() {
        try { return users.findAll(); }
        catch (IOException ex) { throw new IllegalStateException("Unable to load accounts", ex); }
    }

    public Person byId(String id) {
        try { return users.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found.")); }
        catch (IOException ex) { throw new IllegalStateException("Unable to load account", ex); }
    }
}
