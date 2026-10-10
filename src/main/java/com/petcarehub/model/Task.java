package com.petcarehub.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Task {
    private final String id;
    private final String creatorId;
    private String description;
    private int workingHours;
    private double hourlyWage;
    private TaskStatus status;
    private String assignedHelperId;
    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Task(String id, String creatorId, String description, int workingHours, double hourlyWage,
                TaskStatus status, String assignedHelperId, LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = Objects.requireNonNull(id);
        this.creatorId = Objects.requireNonNull(creatorId);
        this.description = Objects.requireNonNull(description);
        this.workingHours = workingHours;
        this.hourlyWage = hourlyWage;
        this.status = Objects.requireNonNull(status);
        this.assignedHelperId = assignedHelperId == null ? "" : assignedHelperId;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.completedAt = completedAt;
    }

    public String getId() { return id; }
    public String getCreatorId() { return creatorId; }
    public String getDescription() { return description; }
    public int getWorkingHours() { return workingHours; }
    public double getHourlyWage() { return hourlyWage; }
    public TaskStatus getStatus() { return status; }
    public String getAssignedHelperId() { return assignedHelperId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public double totalPayment() { return workingHours * hourlyWage; }

    public void assignTo(String helperId) {
        if (status != TaskStatus.OPEN) throw new IllegalStateException("Only open tasks can be assigned.");
        assignedHelperId = helperId;
        status = TaskStatus.ASSIGNED;
    }

    public void complete(String helperId) {
        if (status != TaskStatus.ASSIGNED || !assignedHelperId.equals(helperId)) {
            throw new IllegalStateException("Only the assigned helper can complete this task.");
        }
        status = TaskStatus.COMPLETED;
        completedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status == TaskStatus.COMPLETED) throw new IllegalStateException("Completed tasks cannot be cancelled.");
        status = TaskStatus.CANCELLED;
    }
}
