package com.petcarehub.model;

import java.util.Objects;

public abstract class Person {
    private final String userId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String passwordHash;
    private final Role role;

    protected Person(String userId, String firstName, String lastName, Gender gender,
                     String passwordHash, Role role) {
        this.userId = Objects.requireNonNull(userId);
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.gender = Objects.requireNonNull(gender);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.role = Objects.requireNonNull(role);
    }

    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Gender getGender() { return gender; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }

    public void setFirstName(String firstName) { this.firstName = Objects.requireNonNull(firstName); }
    public void setLastName(String lastName) { this.lastName = Objects.requireNonNull(lastName); }
    public void setGender(Gender gender) { this.gender = Objects.requireNonNull(gender); }
    public void setPasswordHash(String passwordHash) { this.passwordHash = Objects.requireNonNull(passwordHash); }

    public String fullName() {
        return (firstName + " " + lastName).trim();
    }

    @Override
    public String toString() {
        return role + "{" + userId + ", " + fullName() + "}";
    }
}
