package com.petcarehub.ui;

import com.petcarehub.app.AppContext;
import com.petcarehub.model.*;
import com.petcarehub.util.Validation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

final class OwnerPanel extends JPanel {
    private final AppContext context;
    private final String ownerId;
    private final DefaultTableModel petModel = readOnlyModel("Name", "Type", "Age", "Details");
    private final DefaultTableModel taskModel = readOnlyModel("Description", "Hours", "Rate", "Total", "Status", "Created");
    private final JTable petTable = new JTable(petModel);
    private final JTable taskTable = new JTable(taskModel);
    private List<Pet> petRows = new ArrayList<>();
    private List<Task> taskRows = new ArrayList<>();
    private final JLabel summary = new JLabel();

    OwnerPanel(AppContext context, String ownerId) {
        super(new BorderLayout(10, 10));
        this.context = context;
        this.ownerId = ownerId;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        summary.setFont(summary.getFont().deriveFont(Font.BOLD, 14f));
        add(summary, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildPets(), buildTasks());
        split.setResizeWeight(0.45);
        add(split, BorderLayout.CENTER);
        refresh();
    }

    private JComponent buildPets() {
        JPanel panel = Ui.titled("My Pets", new BorderLayout(8, 8));
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(petTable), BorderLayout.CENTER);
        JButton add = new JButton("Add pet");
        JButton remove = new JButton("Remove selected");
        add.addActionListener(e -> showAddPetDialog());
        remove.addActionListener(e -> removePet());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(add); actions.add(remove);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent buildTasks() {
        JPanel panel = Ui.titled("My Care Tasks", new BorderLayout(8, 8));
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        JButton add = new JButton("Publish task");
        JButton cancel = new JButton("Cancel selected");
        JButton refresh = new JButton("Refresh");
        add.addActionListener(e -> showAddTaskDialog());
        cancel.addActionListener(e -> cancelTask());
        refresh.addActionListener(e -> refresh());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refresh); actions.add(cancel); actions.add(add);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddTaskDialog() {
        JTextField description = new JTextField();
        JTextField hours = new JTextField("1");
        JTextField wage = new JTextField("20");
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Description")); form.add(description);
        form.add(new JLabel("Working hours")); form.add(hours);
        form.add(new JLabel("Hourly wage ($)")); form.add(wage);
        if (JOptionPane.showConfirmDialog(this, form, "Publish task", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                context.tasks().create(ownerId, description.getText(),
                        Validation.positiveInt(hours.getText(), "Working hours"),
                        Validation.nonNegativeDouble(wage.getText(), "Hourly wage"));
                refresh();
            } catch (RuntimeException ex) { Ui.error(this, ex); }
        }
    }

    private void showAddPetDialog() {
        JComboBox<PetType> type = new JComboBox<>(PetType.values());
        JTextField name = new JTextField();
        JTextField age = new JTextField("1");
        JTextField breedOrType = new JTextField();
        JComboBox<Dog.Size> size = new JComboBox<>(Dog.Size.values());
        JCheckBox flag = new JCheckBox("Aggressive (dog) / Castrated (cat)");
        JTextField notes = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Type")); form.add(type);
        form.add(new JLabel("Name")); form.add(name);
        form.add(new JLabel("Age")); form.add(age);
        form.add(new JLabel("Breed / exact type")); form.add(breedOrType);
        form.add(new JLabel("Dog size")); form.add(size);
        form.add(new JLabel("Option")); form.add(flag);
        form.add(new JLabel("Medical / care notes")); form.add(notes);

        if (JOptionPane.showConfirmDialog(this, form, "Add pet", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int petAge = Integer.parseInt(age.getText().trim());
                PetType selected = (PetType) type.getSelectedItem();
                if (selected == PetType.DOG) {
                    context.pets().addDog(ownerId, name.getText(), petAge, breedOrType.getText(),
                            (Dog.Size) size.getSelectedItem(), flag.isSelected());
                } else if (selected == PetType.CAT) {
                    context.pets().addCat(ownerId, name.getText(), petAge, breedOrType.getText(),
                            flag.isSelected(), notes.getText());
                } else {
                    context.pets().addOther(ownerId, name.getText(), petAge, breedOrType.getText(),
                            notes.getText(), notes.getText());
                }
                refresh();
            } catch (NumberFormatException ex) {
                Ui.error(this, new IllegalArgumentException("Age must be a whole number."));
            } catch (RuntimeException ex) { Ui.error(this, ex); }
        }
    }

    private void removePet() {
        int row = petTable.getSelectedRow();
        if (row < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Remove " + petRows.get(row).getName() + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                context.pets().deleteOwnedPet(ownerId, petRows.get(row).getId());
                refresh();
            } catch (RuntimeException ex) { Ui.error(this, ex); }
        }
    }

    private void cancelTask() {
        int row = taskTable.getSelectedRow();
        if (row < 0) return;
        try {
            context.tasks().cancel(ownerId, taskRows.get(row).getId(), false);
            refresh();
        } catch (RuntimeException ex) { Ui.error(this, ex); }
    }

    private void refresh() {
        petRows = context.pets().forOwner(ownerId);
        taskRows = context.tasks().forCreator(ownerId);
        petModel.setRowCount(0);
        for (Pet p : petRows) petModel.addRow(new Object[]{p.getName(), p.getType(), p.getAge(), p.details()});
        taskModel.setRowCount(0);
        for (Task t : taskRows) taskModel.addRow(new Object[]{t.getDescription(), t.getWorkingHours(),
                String.format("$%.2f", t.getHourlyWage()), String.format("$%.2f", t.totalPayment()),
                t.getStatus(), t.getCreatedAt().toLocalDate()});
        double activeValue = taskRows.stream().filter(t -> t.getStatus() != TaskStatus.CANCELLED)
                .mapToDouble(Task::totalPayment).sum();
        summary.setText("Pets: " + petRows.size() + "    Published tasks: " + taskRows.size()
                + "    Task value: $" + String.format("%.2f", activeValue));
    }

    private static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }
}
