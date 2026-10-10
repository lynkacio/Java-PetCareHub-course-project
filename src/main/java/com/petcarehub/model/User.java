package com.petcarehub.model;

public final class User extends Person {
    public User(String userId, String firstName, String lastName, Gender gender, String passwordHash) {
        super(userId, firstName, lastName, gender, passwordHash, Role.USER);
    }
}
