package com.petcarehub.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public final class AtomicFiles {
    private AtomicFiles() {}

    public static void writeLines(Path target, List<String> lines) throws IOException {
        Files.createDirectories(target.toAbsolutePath().getParent());
        Path temp = target.resolveSibling(target.getFileName() + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
