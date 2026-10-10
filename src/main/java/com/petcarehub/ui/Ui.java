package com.petcarehub.ui;

import javax.swing.*;
import java.awt.*;

final class Ui {
    private Ui() {}

    static JPanel titled(String title, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return panel;
    }

    static void error(Component parent, Throwable ex) {
        String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "PetCareHub", JOptionPane.INFORMATION_MESSAGE);
    }
}
