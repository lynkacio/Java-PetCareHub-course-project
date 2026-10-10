package com.petcarehub.model;

public final class Cat extends Pet {
    private String breed;
    private boolean castrated;
    private String medicalNeeds;

    public Cat(String id, String ownerId, String name, int age, String breed,
               boolean castrated, String medicalNeeds) {
        super(id, ownerId, name, age, PetType.CAT);
        this.breed = breed;
        this.castrated = castrated;
        this.medicalNeeds = medicalNeeds == null ? "" : medicalNeeds;
    }

    public String getBreed() { return breed; }
    public boolean isCastrated() { return castrated; }
    public String getMedicalNeeds() { return medicalNeeds; }

    @Override
    public String details() {
        String medical = medicalNeeds.isBlank() ? "no special medical needs" : medicalNeeds;
        return breed + " | " + (castrated ? "castrated" : "not castrated") + " | " + medical;
    }
}
