package com.petcarehub.service;

import com.petcarehub.model.*;
import com.petcarehub.repository.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class AnalyticsService {
    public record Snapshot(long users, long helpers, long pets, long tasks, long openTasks,
                           long completedTasks, double marketplaceValue, Map<TaskStatus, Long> statusCounts) {}

    private final UserRepository users;
    private final PetRepository pets;
    private final TaskRepository tasks;

    public AnalyticsService(UserRepository users, PetRepository pets, TaskRepository tasks) {
        this.users = users;
        this.pets = pets;
        this.tasks = tasks;
    }

    public Snapshot snapshot() {
        try {
            List<Person> people = users.findAll();
            List<Task> allTasks = tasks.findAll();
            Map<TaskStatus, Long> counts = allTasks.stream().collect(Collectors.groupingBy(
                    Task::getStatus, () -> new EnumMap<>(TaskStatus.class), Collectors.counting()));
            for (TaskStatus s : TaskStatus.values()) counts.putIfAbsent(s, 0L);
            return new Snapshot(
                    people.stream().filter(p -> p.getRole() == Role.USER).count(),
                    people.stream().filter(p -> p.getRole() == Role.HELPER).count(),
                    pets.findAll().size(), allTasks.size(), counts.get(TaskStatus.OPEN),
                    counts.get(TaskStatus.COMPLETED), allTasks.stream().mapToDouble(Task::totalPayment).sum(), counts);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to calculate analytics", ex);
        }
    }
}
