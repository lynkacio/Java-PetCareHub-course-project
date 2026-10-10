package com.petcarehub.ui;

import com.petcarehub.app.AppContext;
import com.petcarehub.model.Person;

import javax.swing.*;
import java.awt.*;

public final class LoginFrame extends JFrame {
    private final AppContext context;
    private final JTextField userId = new JTextField();
    private final JPasswordField password = new JPasswordField();

    public LoginFrame(AppContext context) {
        this.context = context;
        setTitle("PetCareHub - Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(520, 330);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("PetCareHub", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        JLabel subtitle = new JLabel("Role-based pet care and volunteer task management", SwingConstants.CENTER);
        JPanel heading = new JPanel(new GridLayout(2, 1, 4, 4));
        heading.add(title);
        heading.add(subtitle);
        root.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("User ID"));
        form.add(userId);
        form.add(new JLabel("Password"));
        form.add(password);
        root.add(form, BorderLayout.CENTER);

        JButton login = new JButton("Login");
        JButton register = new JButton("Create account");
        login.addActionListener(e -> login());
        register.addActionListener(e -> new RegisterDialog(this, context).setVisible(true));
        getRootPane().setDefaultButton(login);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(register);
        actions.add(login);
        bottom.add(actions, BorderLayout.NORTH);
        JLabel demo = new JLabel("Demo: owner1 / owner123   |   helper1 / helper123   |   admin / admin123");
        demo.setFont(demo.getFont().deriveFont(11f));
        bottom.add(demo, BorderLayout.SOUTH);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void login() {
        try {
            Person person = context.auth().login(userId.getText(), new String(password.getPassword()));
            dispose();
            new DashboardFrame(context, person).setVisible(true);
        } catch (RuntimeException ex) {
            Ui.error(this, ex);
        }
    }
}
