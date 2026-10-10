package com.petcarehub.app;

import com.petcarehub.model.*;
import com.petcarehub.repository.*;
import com.petcarehub.util.PasswordUtil;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public final class DataSeeder {
    private DataSeeder() {}

    public static void seedIfNeeded(Path dataDir) {
        try {
            Files.createDirectories(dataDir.resolve("certificates"));
            UserRepository users = new UserRepository(dataDir.resolve("users.csv"));
            PetRepository pets = new PetRepository(dataDir.resolve("pets.csv"));
            TaskRepository tasks = new TaskRepository(dataDir.resolve("tasks.csv"));

            if (users.findAll().isEmpty()) {
                users.saveAll(List.of(
                        new Admin("admin", "System", "Admin", Gender.NOT_SPECIFIED, PasswordUtil.hash("admin123")),
                        new User("owner1", "Alex", "Morgan", Gender.NOT_SPECIFIED, PasswordUtil.hash("owner123")),
                        new Helper("helper1", "Jamie", "Lee", Gender.NOT_SPECIFIED, PasswordUtil.hash("helper123"), 8.6, true, "PRE_VERIFIED_DEMO"),
                        new Helper("helper2", "Taylor", "Kim", Gender.NOT_SPECIFIED, PasswordUtil.hash("helper123"), 5.0, false, "")
                ));
            }

            if (pets.findAll().isEmpty()) {
                pets.saveAll(List.of(
                        new Dog(UUID.randomUUID().toString(), "owner1", "Buddy", 4, "Golden Retriever", Dog.Size.LARGE, false),
                        new Cat(UUID.randomUUID().toString(), "owner1", "Milo", 2, "British Shorthair", true, "Sensitive stomach")
                ));
            }

            if (tasks.findAll().isEmpty()) {
                tasks.saveAll(List.of(
                        new Task(UUID.randomUUID().toString(), "owner1", "Evening dog walk", 1, 22.0, TaskStatus.OPEN, "", LocalDateTime.now().minusHours(2), null),
                        new Task(UUID.randomUUID().toString(), "owner1", "Weekend pet sitting", 5, 30.0, TaskStatus.OPEN, "", LocalDateTime.now().minusDays(1), null),
                        new Task(UUID.randomUUID().toString(), "owner1", "Vet appointment transport", 2, 28.0, TaskStatus.OPEN, "", LocalDateTime.now().minusHours(8), null)
                ));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize demo data", ex);
        }
    }
}
