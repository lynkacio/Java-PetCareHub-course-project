package com.petcarehub.ui;

import com.petcarehub.app.AppContext;
import com.petcarehub.model.*;
import com.petcarehub.service.TaskService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class HelperPanel extends JPanel {
    private final AppContext context;
    private final String helperId;
    private final DefaultTableModel openModel = model("Description", "Hours", "Rate", "Total", "Created");
    private final DefaultTableModel myModel = model("Description", "Hours", "Total", "Status", "Owner");
    private final JTable openTable = new JTable(openModel);
    private final JTable myTable = new JTable(myModel);
    private final JTextField search = new JTextField();
    private final JComboBox<TaskService.SortMode> sort = new JComboBox<>(TaskService.SortMode.values());
    private final JLabel status = new JLabel();
    private List<Task> openRows = new ArrayList<>();
    private List<Task> myRows = new ArrayList<>();

    HelperPanel(AppContext context, String helperId) {
        super(new BorderLayout(10, 10));
        this.context = context;
        this.helperId = helperId;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildProfile(), BorderLayout.NORTH);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildMarketplace(), buildMyTasks());
        split.setResizeWeight(0.58);
        add(split, BorderLayout.CENTER);
        refreshAll();
    }

    private JComponent buildProfile() {
        JPanel panel = Ui.titled("Helper Profile & Verification", new BorderLayout(8, 8));
        panel.add(status, BorderLayout.CENTER);
        JButton upload = new JButton("Upload PDF certificate");
        upload.addActionListener(e -> uploadCertificate());
        JButton refresh = new JButton("Refresh status");
        refresh.addActionListener(e -> refreshAll());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refresh); actions.add(upload);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JComponent buildMarketplace() {
        JPanel panel = Ui.titled("Open Task Marketplace", new BorderLayout(8, 8));
        JPanel filters = new JPanel(new BorderLayout(8, 8));
        filters.add(new JLabel("Search"), BorderLayout.WEST);
        filters.add(search, BorderLayout.CENTER);
        filters.add(sort, BorderLayout.EAST);
        panel.add(filters, BorderLayout.NORTH);
        openTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(openTable), BorderLayout.CENTER);
        JButton accept = new JButton("Accept selected task");
        accept.addActionListener(e -> acceptTask());
        panel.add(accept, BorderLayout.SOUTH);
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshOpen(); }
            public void removeUpdate(DocumentEvent e) { refreshOpen(); }
            public void changedUpdate(DocumentEvent e) { refreshOpen(); }
        });
        sort.addActionListener(e -> refreshOpen());
        return panel;
    }

    private JComponent buildMyTasks() {
        JPanel panel = Ui.titled("My Accepted Tasks", new BorderLayout(8, 8));
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(myTable), BorderLayout.CENTER);
        JButton complete = new JButton("Mark selected as completed");
        complete.addActionListener(e -> completeTask());
        panel.add(complete, BorderLayout.SOUTH);
        return panel;
    }

    private void uploadCertificate() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                context.certificates().upload(helperId, chooser.getSelectedFile().toPath());
                Ui.info(this, "Certificate uploaded. An admin must approve it before you can accept tasks.");
                refreshAll();
            } catch (RuntimeException ex) { Ui.error(this, ex); }
        }
    }

    private void acceptTask() {
        int row = openTable.getSelectedRow();
        if (row < 0) return;
        try {
            context.tasks().accept(helperId, openRows.get(row).getId());
            refreshAll();
        } catch (RuntimeException ex) { Ui.error(this, ex); }
    }

    private void completeTask() {
        int row = myTable.getSelectedRow();
        if (row < 0) return;
        Task task = myRows.get(row);
        if (task.getStatus() != TaskStatus.ASSIGNED) {
            Ui.info(this, "Only assigned tasks can be completed.");
            return;
        }
        try {
            context.tasks().complete(helperId, task.getId());
            refreshAll();
        } catch (RuntimeException ex) { Ui.error(this, ex); }
    }

    private void refreshAll() {
        Person p = context.accounts().byId(helperId);
        Helper h = (Helper) p;
        double earnings = context.tasks().forHelper(helperId).stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED).mapToDouble(Task::totalPayment).sum();
        status.setText(String.format("%s  |  Verified: %s  |  Credit: %.1f/10  |  Completed earnings: $%.2f",
                h.fullName(), h.isVerified() ? "YES" : "NO", h.getCreditScore(), earnings));
        refreshOpen();
        refreshMine();
    }

    private void refreshOpen() {
        TaskService.SortMode mode = (TaskService.SortMode) sort.getSelectedItem();
        openRows = context.tasks().searchOpen(search.getText(), mode == null ? TaskService.SortMode.NEWEST : mode);
        openModel.setRowCount(0);
        for (Task t : openRows) {
            openModel.addRow(new Object[]{t.getDescription(), t.getWorkingHours(), String.format("$%.2f", t.getHourlyWage()),
                    String.format("$%.2f", t.totalPayment()), t.getCreatedAt().toLocalDate()});
        }
    }

    private void refreshMine() {
        myRows = context.tasks().forHelper(helperId);
        myModel.setRowCount(0);
        for (Task t : myRows) {
            myModel.addRow(new Object[]{t.getDescription(), t.getWorkingHours(), String.format("$%.2f", t.totalPayment()),
                    t.getStatus(), t.getCreatorId()});
        }
    }

    private static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }
}
