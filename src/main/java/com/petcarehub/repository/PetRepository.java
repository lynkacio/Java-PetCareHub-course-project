package com.petcarehub.repository;

import com.petcarehub.model.*;
import com.petcarehub.util.AtomicFiles;
import com.petcarehub.util.Csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class PetRepository implements Repository<Pet> {
    private final Path path;

    public PetRepository(Path path) { this.path = path; }

    @Override
    public synchronized List<Pet> findAll() throws IOException {
        if (!Files.exists(path)) return new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Pet> pets = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            List<String> f = Csv.parse(lines.get(i));
            if (f.size() < 13) continue;
            String id = f.get(0);
            String ownerId = f.get(1);
            PetType type = PetType.valueOf(f.get(2));
            String name = f.get(3);
            int age = Integer.parseInt(f.get(4));
            Pet pet = switch (type) {
                case DOG -> new Dog(id, ownerId, name, age, f.get(5), Dog.Size.valueOf(f.get(6)), Boolean.parseBoolean(f.get(7)));
                case CAT -> new Cat(id, ownerId, name, age, f.get(5), Boolean.parseBoolean(f.get(8)), f.get(9));
                case OTHER -> new OtherPet(id, ownerId, name, age, f.get(10), f.get(11), f.get(12));
            };
            pets.add(pet);
        }
        return pets;
    }

    @Override
    public synchronized void saveAll(List<Pet> pets) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("id,ownerId,type,name,age,breed,size,aggressive,castrated,medicalNeeds,exactType,description,careInstructions");
        for (Pet p : pets) {
            String breed = "", size = "SMALL", aggressive = "false", castrated = "false", medical = "";
            String exactType = "", description = "", care = "";
            if (p instanceof Dog d) {
                breed = d.getBreed(); size = d.getSize().name(); aggressive = String.valueOf(d.isAggressive());
            } else if (p instanceof Cat c) {
                breed = c.getBreed(); castrated = String.valueOf(c.isCastrated()); medical = c.getMedicalNeeds();
            } else if (p instanceof OtherPet o) {
                exactType = o.getExactType(); description = o.getDescription(); care = o.getCareInstructions();
            }
            lines.add(Csv.row(List.of(
                    p.getId(), p.getOwnerId(), p.getType().name(), p.getName(), String.valueOf(p.getAge()),
                    breed, size, aggressive, castrated, medical, exactType, description, care)));
        }
        AtomicFiles.writeLines(path, lines);
    }
}
