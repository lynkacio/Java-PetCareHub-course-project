package com.petcarehub.app;

import com.petcarehub.ui.LoginFrame;

import javax.swing.*;
import java.nio.file.Path;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        Path dataDir = Path.of(System.getProperty("petcare.dataDir", "data"));
        DataSeeder.seedIfNeeded(dataDir);
        AppContext context = new AppContext(dataDir);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame(context).setVisible(true);
        });
    }
}
