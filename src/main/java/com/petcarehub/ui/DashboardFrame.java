package com.petcarehub.ui;

import com.petcarehub.app.AppContext;
import com.petcarehub.model.Person;
import com.petcarehub.model.Role;

import javax.swing.*;
import java.awt.*;

public final class DashboardFrame extends JFrame {
    private final AppContext context;
    private final Person signedIn;

    public DashboardFrame(AppContext context, Person signedIn) {
        this.context = context;
        this.signedIn = signedIn;
        setTitle("PetCareHub - " + signedIn.getRole() + " Dashboard");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1120, 720);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildHeader(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        if (signedIn.getRole() == Role.USER) {
            tabs.addTab("My Workspace", new OwnerPanel(context, signedIn.getUserId()));
        } else if (signedIn.getRole() == Role.HELPER) {
            tabs.addTab("Task Marketplace", new HelperPanel(context, signedIn.getUserId()));
        } else {
            tabs.addTab("Administration", new AdminPanel(context, signedIn.getUserId()));
        }
        add(tabs, BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel identity = new JLabel("Signed in as " + signedIn.fullName() + "  •  " + signedIn.getRole() + "  •  " + signedIn.getUserId());
        identity.setFont(identity.getFont().deriveFont(Font.BOLD, 14f));
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame(context).setVisible(true);
        });
        panel.add(identity, BorderLayout.WEST);
        panel.add(logout, BorderLayout.EAST);
        return panel;
    }
}
