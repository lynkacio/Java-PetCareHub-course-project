package com.petcarehub.service;

import com.petcarehub.model.*;
import com.petcarehub.repository.TaskRepository;
import com.petcarehub.repository.UserRepository;
import com.petcarehub.util.AuditLog;
import com.petcarehub.util.Validation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public final class TaskService {
    public enum SortMode { NEWEST, HIGHEST_PAY, SHORTEST_DURATION }

    private final TaskRepository tasks;
    private final UserRepository users;
    private final AuditLog audit;

    public TaskService(TaskRepository tasks, UserRepository users, AuditLog audit) {
        this.tasks = tasks;
        this.users = users;
        this.audit = audit;
    }

    public Task create(String creatorId, String description, int hours, double hourlyWage) {
        Validation.required(description, "Description");
        if (hours <= 0 || hours > 168) throw new IllegalArgumentException("Working hours must be between 1 and 168.");
        if (hourlyWage < 0 || hourlyWage > 1000) throw new IllegalArgumentException("Hourly wage must be between 0 and 1000.");
        Task task = new Task(UUID.randomUUID().toString(), creatorId, description.trim(), hours, hourlyWage,
                TaskStatus.OPEN, "", LocalDateTime.now(), null);
        mutate(all -> all.add(task));
        audit.record(creatorId, "CREATE_TASK id=" + task.getId());
        return task;
    }

    public List<Task> forCreator(String creatorId) {
        return all().stream().filter(t -> t.getCreatorId().equals(creatorId))
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed()).toList();
    }

    public List<Task> forHelper(String helperId) {
        return all().stream().filter(t -> helperId.equals(t.getAssignedHelperId()))
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed()).toList();
    }

    public List<Task> searchOpen(String query, SortMode sortMode) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        Stream<Task> stream = all().stream().filter(t -> t.getStatus() == TaskStatus.OPEN)
                .filter(t -> t.getDescription().toLowerCase(Locale.ROOT).contains(q));
        Comparator<Task> comparator = switch (sortMode) {
            case HIGHEST_PAY -> Comparator.comparingDouble(Task::totalPayment).reversed();
            case SHORTEST_DURATION -> Comparator.comparingInt(Task::getWorkingHours);
            case NEWEST -> Comparator.comparing(Task::getCreatedAt).reversed();
        };
        return stream.sorted(comparator).toList();
    }

    public void accept(String helperId, String taskId) {
        Helper helper = requireHelper(helperId);
        if (!helper.isVerified()) throw new IllegalStateException("Upload a certificate and wait for admin verification before accepting tasks.");
        mutate(all -> requireTask(all, taskId).assignTo(helperId));
        audit.record(helperId, "ACCEPT_TASK id=" + taskId);
    }

    public void complete(String helperId, String taskId) {
        mutate(all -> requireTask(all, taskId).complete(helperId));
        try {
            List<Person> people = users.findAll();
            people.stream().filter(p -> p instanceof Helper && p.getUserId().equals(helperId))
                    .map(p -> (Helper) p).findFirst().ifPresent(h -> h.adjustCreditScore(0.2));
            users.saveAll(people);
        } catch (IOException ex) {
            throw new IllegalStateException("Task completed but helper score could not be saved", ex);
        }
        audit.record(helperId, "COMPLETE_TASK id=" + taskId);
    }

    public void cancel(String actorId, String taskId, boolean admin) {
        mutate(all -> {
            Task task = requireTask(all, taskId);
            if (!admin && !task.getCreatorId().equals(actorId)) {
                throw new IllegalStateException("You can only cancel your own task.");
            }
            task.cancel();
        });
        audit.record(actorId, "CANCEL_TASK id=" + taskId);
    }

    public List<Task> all() {
        try { return tasks.findAll(); }
        catch (IOException ex) { throw new IllegalStateException("Unable to load tasks", ex); }
    }

    private Helper requireHelper(String helperId) {
        try {
            Person p = users.findById(helperId).orElseThrow(() -> new IllegalStateException("Helper not found."));
            if (!(p instanceof Helper h)) throw new IllegalStateException("Account is not a helper.");
            return h;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load helper", ex);
        }
    }

    private Task requireTask(List<Task> all, String taskId) {
        return all.stream().filter(t -> t.getId().equals(taskId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found."));
    }

    private void mutate(java.util.function.Consumer<List<Task>> change) {
        try {
            List<Task> all = new ArrayList<>(tasks.findAll());
            change.accept(all);
            tasks.saveAll(all);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to update tasks", ex);
        }
    }
}
