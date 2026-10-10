package com.petcarehub.model;

import java.util.Objects;

public abstract class Pet {
    private final String id;
    private final String ownerId;
    private String name;
    private int age;
    private final PetType type;

    protected Pet(String id, String ownerId, String name, int age, PetType type) {
        this.id = Objects.requireNonNull(id);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.name = Objects.requireNonNull(name);
        this.age = age;
        this.type = Objects.requireNonNull(type);
    }

    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public PetType getType() { return type; }

    public void setName(String name) { this.name = Objects.requireNonNull(name); }
    public void setAge(int age) { this.age = age; }

    public abstract String details();
}
