package com.petcarehub.repository;

import com.petcarehub.model.*;
import com.petcarehub.util.AtomicFiles;
import com.petcarehub.util.Csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class UserRepository implements Repository<Person> {
    private final Path path;

    public UserRepository(Path path) { this.path = path; }

    @Override
    public synchronized List<Person> findAll() throws IOException {
        if (!Files.exists(path)) return new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Person> people = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            List<String> f = Csv.parse(lines.get(i));
            if (f.size() < 9) continue;
            String id = f.get(0);
            String first = f.get(1);
            String last = f.get(2);
            Gender gender = Gender.valueOf(f.get(3));
            Role role = Role.valueOf(f.get(4));
            String hash = f.get(5);
            double credit = f.get(6).isBlank() ? 5.0 : Double.parseDouble(f.get(6));
            boolean verified = Boolean.parseBoolean(f.get(7));
            String cert = f.get(8);
            Person person = switch (role) {
                case USER -> new User(id, first, last, gender, hash);
                case HELPER -> new Helper(id, first, last, gender, hash, credit, verified, cert);
                case ADMIN -> new Admin(id, first, last, gender, hash);
            };
            people.add(person);
        }
        return people;
    }

    @Override
    public synchronized void saveAll(List<Person> people) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("userId,firstName,lastName,gender,role,passwordHash,creditScore,verified,certificatePath");
        for (Person p : people) {
            double credit = p instanceof Helper h ? h.getCreditScore() : 0;
            boolean verified = p instanceof Helper h && h.isVerified();
            String cert = p instanceof Helper h ? h.getCertificatePath() : "";
            lines.add(Csv.row(List.of(
                    p.getUserId(), p.getFirstName(), p.getLastName(), p.getGender().name(),
                    p.getRole().name(), p.getPasswordHash(), String.valueOf(credit),
                    String.valueOf(verified), cert)));
        }
        AtomicFiles.writeLines(path, lines);
    }

    public Optional<Person> findById(String userId) throws IOException {
        return findAll().stream().filter(p -> p.getUserId().equalsIgnoreCase(userId)).findFirst();
    }
}
