package com.petcarehub;

import com.petcarehub.app.AppContext;
import com.petcarehub.app.DataSeeder;
import com.petcarehub.model.*;
import com.petcarehub.util.PasswordUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public final class SmokeTest {
    public static void main(String[] args) throws Exception {
        testPasswordHashing();
        testTaskLifecycleAndPersistence();
        testPetPolymorphism();
        System.out.println("All smoke tests passed.");
    }

    private static void testPasswordHashing() {
        String hash = PasswordUtil.hash("secret123");
        assert hash.startsWith("pbkdf2$");
        assert PasswordUtil.verify("secret123", hash);
        assert !PasswordUtil.verify("wrong", hash);
    }

    private static void testTaskLifecycleAndPersistence() throws Exception {
        Path dir = Files.createTempDirectory("petcarehub-test-");
        DataSeeder.seedIfNeeded(dir);
        AppContext ctx = new AppContext(dir);

        Task task = ctx.tasks().create("owner1", "Test walking task", 2, 25.0);
        assert task.getStatus() == TaskStatus.OPEN;
        assert Math.abs(task.totalPayment() - 50.0) < 0.001;

        ctx.tasks().accept("helper1", task.getId());
        Task assigned = ctx.tasks().all().stream().filter(t -> t.getId().equals(task.getId())).findFirst().orElseThrow();
        assert assigned.getStatus() == TaskStatus.ASSIGNED;
        assert assigned.getAssignedHelperId().equals("helper1");

        ctx.tasks().complete("helper1", task.getId());
        Task completed = ctx.tasks().all().stream().filter(t -> t.getId().equals(task.getId())).findFirst().orElseThrow();
        assert completed.getStatus() == TaskStatus.COMPLETED;
        assert completed.getCompletedAt() != null;
    }

    private static void testPetPolymorphism() throws Exception {
        Path dir = Files.createTempDirectory("petcarehub-pet-test-");
        DataSeeder.seedIfNeeded(dir);
        AppContext ctx = new AppContext(dir);
        Pet pet = ctx.pets().addOther("owner1", "Kiwi", 2, "Parrot", "Talkative", "Fresh water daily");
        assert pet instanceof OtherPet;
        assert pet.details().contains("Parrot");
        assert ctx.pets().forOwner("owner1").stream().anyMatch(p -> p.getId().equals(pet.getId()));
    }
}
