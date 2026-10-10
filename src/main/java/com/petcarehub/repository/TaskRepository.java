package com.petcarehub.repository;

import com.petcarehub.model.*;
import com.petcarehub.util.AtomicFiles;
import com.petcarehub.util.Csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public final class TaskRepository implements Repository<Task> {
    private final Path path;

    public TaskRepository(Path path) { this.path = path; }

    @Override
    public synchronized List<Task> findAll() throws IOException {
        if (!Files.exists(path)) return new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            List<String> f = Csv.parse(lines.get(i));
            if (f.size() < 9) continue;
            LocalDateTime completed = f.get(8).isBlank() ? null : LocalDateTime.parse(f.get(8));
            tasks.add(new Task(
                    f.get(0), f.get(1), f.get(2), Integer.parseInt(f.get(3)), Double.parseDouble(f.get(4)),
                    TaskStatus.valueOf(f.get(5)), f.get(6), LocalDateTime.parse(f.get(7)), completed));
        }
        return tasks;
    }

    @Override
    public synchronized void saveAll(List<Task> tasks) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("id,creatorId,description,workingHours,hourlyWage,status,assignedHelperId,createdAt,completedAt");
        for (Task t : tasks) {
            lines.add(Csv.row(List.of(
                    t.getId(), t.getCreatorId(), t.getDescription(), String.valueOf(t.getWorkingHours()),
                    String.valueOf(t.getHourlyWage()), t.getStatus().name(), t.getAssignedHelperId(),
                    t.getCreatedAt().toString(), t.getCompletedAt() == null ? "" : t.getCompletedAt().toString())));
        }
        AtomicFiles.writeLines(path, lines);
    }
}
