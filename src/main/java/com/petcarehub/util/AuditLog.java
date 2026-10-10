package com.petcarehub.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;

public final class AuditLog {
    private final Path path;

    public AuditLog(Path path) { this.path = path; }

    public synchronized void record(String actorId, String action) {
        try {
            Files.createDirectories(path.toAbsolutePath().getParent());
            String line = LocalDateTime.now() + " | " + actorId + " | " + action + System.lineSeparator();
            Files.writeString(path, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.err.println("Audit logging failed: " + ex.getMessage());
        }
    }
}
