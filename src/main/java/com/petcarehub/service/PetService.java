package com.petcarehub.service;

import com.petcarehub.model.*;
import com.petcarehub.repository.PetRepository;
import com.petcarehub.util.AuditLog;
import com.petcarehub.util.Validation;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class PetService {
    private final PetRepository pets;
    private final AuditLog audit;

    public PetService(PetRepository pets, AuditLog audit) {
        this.pets = pets;
        this.audit = audit;
    }

    public List<Pet> forOwner(String ownerId) {
        try {
            return pets.findAll().stream().filter(p -> p.getOwnerId().equals(ownerId)).collect(Collectors.toList());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load pets", ex);
        }
    }

    public List<Pet> all() {
        try { return pets.findAll(); }
        catch (IOException ex) { throw new IllegalStateException("Unable to load pets", ex); }
    }

    public Pet addDog(String ownerId, String name, int age, String breed, Dog.Size size, boolean aggressive) {
        return add(new Dog(UUID.randomUUID().toString(), ownerId, Validation.required(name, "Pet name"), age,
                Validation.required(breed, "Breed"), size, aggressive));
    }

    public Pet addCat(String ownerId, String name, int age, String breed, boolean castrated, String medicalNeeds) {
        return add(new Cat(UUID.randomUUID().toString(), ownerId, Validation.required(name, "Pet name"), age,
                Validation.required(breed, "Breed"), castrated, medicalNeeds));
    }

    public Pet addOther(String ownerId, String name, int age, String type, String description, String care) {
        return add(new OtherPet(UUID.randomUUID().toString(), ownerId, Validation.required(name, "Pet name"), age,
                Validation.required(type, "Pet type"), description, care));
    }

    private Pet add(Pet pet) {
        if (pet.getAge() < 0 || pet.getAge() > 100) throw new IllegalArgumentException("Pet age must be between 0 and 100.");
        try {
            List<Pet> all = pets.findAll();
            all.add(pet);
            pets.saveAll(all);
            audit.record(pet.getOwnerId(), "ADD_PET id=" + pet.getId() + " type=" + pet.getType());
            return pet;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save pet", ex);
        }
    }

    public void deleteOwnedPet(String ownerId, String petId) {
        try {
            List<Pet> all = pets.findAll();
            boolean removed = all.removeIf(p -> p.getId().equals(petId) && p.getOwnerId().equals(ownerId));
            if (!removed) throw new IllegalArgumentException("Pet not found or not owned by current user.");
            pets.saveAll(all);
            audit.record(ownerId, "DELETE_PET id=" + petId);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to delete pet", ex);
        }
    }
}
