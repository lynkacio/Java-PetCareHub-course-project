package com.petcarehub.model;

public final class Dog extends Pet {
    public enum Size { SMALL, MEDIUM, LARGE, GIANT }

    private String breed;
    private Size size;
    private boolean aggressive;

    public Dog(String id, String ownerId, String name, int age, String breed, Size size, boolean aggressive) {
        super(id, ownerId, name, age, PetType.DOG);
        this.breed = breed;
        this.size = size;
        this.aggressive = aggressive;
    }

    public String getBreed() { return breed; }
    public Size getSize() { return size; }
    public boolean isAggressive() { return aggressive; }

    @Override
    public String details() {
        return breed + " | " + size + (aggressive ? " | caution" : "");
    }
}
