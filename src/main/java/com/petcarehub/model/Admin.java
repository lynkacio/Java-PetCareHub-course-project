package com.petcarehub.model;

public final class Admin extends Person {
    public Admin(String userId, String firstName, String lastName, Gender gender, String passwordHash) {
        super(userId, firstName, lastName, gender, passwordHash, Role.ADMIN);
    }
}
