package com.petcarehub.service;

import com.petcarehub.model.*;
import com.petcarehub.repository.UserRepository;
import com.petcarehub.util.AuditLog;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public final class CertificateService {
    private final UserRepository users;
    private final Path certificateDir;
    private final AuditLog audit;

    public CertificateService(UserRepository users, Path certificateDir, AuditLog audit) {
        this.users = users;
        this.certificateDir = certificateDir;
        this.audit = audit;
    }

    public void upload(String helperId, Path source) {
        String fileName = source.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".pdf")) throw new IllegalArgumentException("Certificate must be a PDF file.");
        try {
            Files.createDirectories(certificateDir);
            Path destination = certificateDir.resolve(helperId + "_certificate.pdf");
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            List<Person> all = users.findAll();
            Helper helper = all.stream().filter(p -> p instanceof Helper && p.getUserId().equals(helperId))
                    .map(p -> (Helper) p).findFirst().orElseThrow();
            helper.setCertificatePath(destination.toString().replace('\\', '/'));
            helper.setVerified(false);
            users.saveAll(all);
            audit.record(helperId, "UPLOAD_CERTIFICATE");
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to upload certificate", ex);
        }
    }

    public void setVerified(String adminId, String helperId, boolean verified) {
        try {
            List<Person> all = users.findAll();
            Helper helper = all.stream().filter(p -> p instanceof Helper && p.getUserId().equals(helperId))
                    .map(p -> (Helper) p).findFirst().orElseThrow(() -> new IllegalArgumentException("Helper not found."));
            if (verified && helper.getCertificatePath().isBlank()) {
                throw new IllegalStateException("This helper has not uploaded a certificate.");
            }
            helper.setVerified(verified);
            users.saveAll(all);
            audit.record(adminId, "SET_HELPER_VERIFICATION helper=" + helperId + " verified=" + verified);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to update helper verification", ex);
        }
    }
}
