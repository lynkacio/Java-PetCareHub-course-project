package com.petcarehub.ui;

import com.petcarehub.app.AppContext;
import com.petcarehub.model.*;
import com.petcarehub.service.AnalyticsService;
import com.petcarehub.util.Csv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

final class AdminPanel extends JPanel {
    private final AppContext context;
    private final String adminId;
    private final JLabel metrics = new JLabel();
    private final StatusBarChart chart = new StatusBarChart();
    private final DefaultTableModel helperModel = model("User ID", "Name", "Credit", "Verified", "Certificate");
    private final DefaultTableModel taskModel = model("ID", "Creator", "Description", "Hours", "Total", "Status", "Helper");
    private final JTable helperTable = new JTable(helperModel);
    private final JTable taskTable = new JTable(taskModel);
    private List<Helper> helperRows = new ArrayList<>();
    private List<Task> taskRows = new ArrayList<>();

    AdminPanel(AppContext context, String adminId) {
        super(new BorderLayout(10, 10));
        this.context = context;
        this.adminId = adminId;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildAnalytics(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Helper Verification", buildHelpers());
        tabs.addTab("Task Operations", buildTasks());
        add(tabs, BorderLayout.CENTER);
        refresh();
    }

    private JComponent buildAnalytics() {
        JPanel panel = Ui.titled("System Analytics", new BorderLayout(12, 12));
        metrics.setFont(metrics.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(metrics, BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);
        JButton refresh = new JButton("Refresh analytics");
        refresh.addActionListener(e -> refresh());
        panel.add(refresh, BorderLayout.EAST);
        return panel;
    }

    private JComponent buildHelpers() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        helperTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(helperTable), BorderLayout.CENTER);
        JButton approve = new JButton("Approve selected");
        JButton revoke = new JButton("Revoke verification");
        approve.addActionListener(e -> verifySelected(true));
        revoke.addActionListener(e -> verifySelected(false));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(revoke); actions.add(approve);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent buildTasks() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        JButton cancel = new JButton("Cancel selected");
        JButton export = new JButton("Export CSV report");
        cancel.addActionListener(e -> cancelSelected());
        export.addActionListener(e -> exportReport());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(export); actions.add(cancel);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void verifySelected(boolean verified) {
        int row = helperTable.getSelectedRow();
        if (row < 0) return;
        try {
            context.certificates().setVerified(adminId, helperRows.get(row).getUserId(), verified);
            refresh();
        } catch (RuntimeException ex) { Ui.error(this, ex); }
    }

    private void cancelSelected() {
        int row = taskTable.getSelectedRow();
        if (row < 0) return;
        try {
            context.tasks().cancel(adminId, taskRows.get(row).getId(), true);
            refresh();
        } catch (RuntimeException ex) { Ui.error(this, ex); }
    }

    private void exportReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("petcarehub-task-report.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        List<String> lines = new ArrayList<>();
        lines.add("id,creator,description,hours,hourlyWage,total,status,assignedHelper,createdAt,completedAt");
        for (Task t : taskRows) {
            lines.add(Csv.row(List.of(t.getId(), t.getCreatorId(), t.getDescription(), String.valueOf(t.getWorkingHours()),
                    String.valueOf(t.getHourlyWage()), String.valueOf(t.totalPayment()), t.getStatus().name(),
                    t.getAssignedHelperId(), t.getCreatedAt().toString(), t.getCompletedAt() == null ? "" : t.getCompletedAt().toString())));
        }
        try {
            Files.write(chooser.getSelectedFile().toPath(), lines, StandardCharsets.UTF_8);
            Ui.info(this, "Report exported successfully.");
        } catch (IOException ex) { Ui.error(this, ex); }
    }

    private void refresh() {
        AnalyticsService.Snapshot s = context.analytics().snapshot();
        metrics.setText(String.format("Users: %d    Helpers: %d    Pets: %d    Tasks: %d    Open: %d    Completed: %d    Marketplace value: $%.2f",
                s.users(), s.helpers(), s.pets(), s.tasks(), s.openTasks(), s.completedTasks(), s.marketplaceValue()));
        chart.setCounts(s.statusCounts());

        helperRows = context.accounts().all().stream().filter(p -> p instanceof Helper).map(p -> (Helper) p).toList();
        helperModel.setRowCount(0);
        for (Helper h : helperRows) {
            helperModel.addRow(new Object[]{h.getUserId(), h.fullName(), String.format("%.1f", h.getCreditScore()),
                    h.isVerified(), h.getCertificatePath().isBlank() ? "Not uploaded" : h.getCertificatePath()});
        }

        taskRows = context.tasks().all();
        taskModel.setRowCount(0);
        for (Task t : taskRows) {
            taskModel.addRow(new Object[]{t.getId().substring(0, Math.min(8, t.getId().length())), t.getCreatorId(),
                    t.getDescription(), t.getWorkingHours(), String.format("$%.2f", t.totalPayment()), t.getStatus(),
                    t.getAssignedHelperId().isBlank() ? "-" : t.getAssignedHelperId()});
        }
    }

    private static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }
}
