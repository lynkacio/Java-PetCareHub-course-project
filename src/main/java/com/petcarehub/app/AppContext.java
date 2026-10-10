package com.petcarehub.app;

import com.petcarehub.repository.*;
import com.petcarehub.service.*;
import com.petcarehub.util.AuditLog;

import java.nio.file.Path;

public final class AppContext {
    private final AuthService authService;
    private final PetService petService;
    private final TaskService taskService;
    private final CertificateService certificateService;
    private final AnalyticsService analyticsService;
    private final AccountService accountService;

    public AppContext(Path dataDir) {
        UserRepository users = new UserRepository(dataDir.resolve("users.csv"));
        PetRepository pets = new PetRepository(dataDir.resolve("pets.csv"));
        TaskRepository tasks = new TaskRepository(dataDir.resolve("tasks.csv"));
        AuditLog audit = new AuditLog(dataDir.resolve("audit.log"));
        this.authService = new AuthService(users, audit);
        this.petService = new PetService(pets, audit);
        this.taskService = new TaskService(tasks, users, audit);
        this.certificateService = new CertificateService(users, dataDir.resolve("certificates"), audit);
        this.analyticsService = new AnalyticsService(users, pets, tasks);
        this.accountService = new AccountService(users);
    }

    public AuthService auth() { return authService; }
    public PetService pets() { return petService; }
    public TaskService tasks() { return taskService; }
    public CertificateService certificates() { return certificateService; }
    public AnalyticsService analytics() { return analyticsService; }
    public AccountService accounts() { return accountService; }
}
