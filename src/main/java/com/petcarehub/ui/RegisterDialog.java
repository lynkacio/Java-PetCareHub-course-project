package com.petcarehub.ui;

import com.petcarehub.app.AppContext;
import com.petcarehub.model.*;

import javax.swing.*;
import java.awt.*;

final class RegisterDialog extends JDialog {
    private final AppContext context;
    private final JTextField userId = new JTextField();
    private final JTextField firstName = new JTextField();
    private final JTextField lastName = new JTextField();
    private final JComboBox<Gender> gender = new JComboBox<>(Gender.values());
    private final JComboBox<Role> role = new JComboBox<>(new Role[]{Role.USER, Role.HELPER});
    private final JPasswordField password = new JPasswordField();

    RegisterDialog(Frame owner, AppContext context) {
        super(owner, "Create account", true);
        this.context = context;
        setSize(440, 360);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        form.add(new JLabel("User ID")); form.add(userId);
        form.add(new JLabel("First name")); form.add(firstName);
        form.add(new JLabel("Last name")); form.add(lastName);
        form.add(new JLabel("Gender")); form.add(gender);
        form.add(new JLabel("Role")); form.add(role);
        form.add(new JLabel("Password")); form.add(password);
        add(form, BorderLayout.CENTER);

        JButton create = new JButton("Create account");
        create.addActionListener(e -> create());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(create);
        add(actions, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(create);
    }

    private void create() {
        try {
            context.auth().register(userId.getText(), firstName.getText(), lastName.getText(),
                    (Gender) gender.getSelectedItem(), new String(password.getPassword()),
                    (Role) role.getSelectedItem());
            Ui.info(this, "Account created. You can now log in.");
            dispose();
        } catch (RuntimeException ex) {
            Ui.error(this, ex);
        }
    }
}
