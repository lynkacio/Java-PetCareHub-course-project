package com.petcarehub.model;

public final class OtherPet extends Pet {
    private String exactType;
    private String description;
    private String careInstructions;

    public OtherPet(String id, String ownerId, String name, int age, String exactType,
                    String description, String careInstructions) {
        super(id, ownerId, name, age, PetType.OTHER);
        this.exactType = exactType;
        this.description = description == null ? "" : description;
        this.careInstructions = careInstructions == null ? "" : careInstructions;
    }

    public String getExactType() { return exactType; }
    public String getDescription() { return description; }
    public String getCareInstructions() { return careInstructions; }

    @Override
    public String details() {
        String care = careInstructions.isBlank() ? "standard care" : careInstructions;
        return exactType + " | " + care;
    }
}
