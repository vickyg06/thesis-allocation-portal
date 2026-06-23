/*
 * Supervision Plan
 * Author: Viktoria Gospodinova
 * Last Change:  02.06.26
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import user.Role;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminDashboard {

    public AdminDashboard(user.Admin loggedInAdmin, SessionFactory factory) {

        JFrame frame = new JFrame();
        frame.setSize(850, 600); // Made it slightly wider for the new layout
        frame.setTitle("Admin Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(0xEAE0CF));
        frame.setLayout(null);

        //Logo
        ImageIcon logo = new ImageIcon("logo.png");
        frame.setIconImage(logo.getImage());

        // Welcome Message
        JLabel welcomeLabel = new JLabel("Admin Control Panel: " + loggedInAdmin.getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        welcomeLabel.setBounds(50, 20, 700, 40);
        frame.add(welcomeLabel);

        //Fetch Users to display on the left
        Session session = factory.openSession();
        List<user.Student> allStudents = null;
        List<user.Assistant> allAssistants = null;

        try {
            allStudents = session.createQuery("FROM Student", user.Student.class).list();
            allAssistants = session.createQuery("FROM Assistant", user.Assistant.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        // --- VIEW EXISTING USERS
        JLabel studentTitle = new JLabel("Registered Students:");
        studentTitle.setFont(new Font("Arial", Font.BOLD, 14));
        studentTitle.setBounds(50, 80, 200, 20);
        frame.add(studentTitle);

        //fetch students
        StringBuilder studentText = new StringBuilder();
        if (allStudents != null) {
            for (user.Student s : allStudents) {
                studentText.append(s.getName()).append("\n");
            }
        }
        JTextArea studentArea = new JTextArea(studentText.toString());
        studentArea.setEditable(false);
        JScrollPane studentScroll = new JScrollPane(studentArea);
        studentScroll.setBounds(50, 110, 200, 400);
        frame.add(studentScroll);

        JLabel assistantTitle = new JLabel("University Assistants:");
        assistantTitle.setFont(new Font("Arial", Font.BOLD, 14));
        assistantTitle.setBounds(280, 80, 200, 20);
        frame.add(assistantTitle);

        //fetch assistants
        StringBuilder assistantText = new StringBuilder();
        if (allAssistants != null) {
            for (user.Assistant a : allAssistants) {
                assistantText.append(a.getName()).append("\n");
            }
        }
        JTextArea assistantArea = new JTextArea(assistantText.toString());
        assistantArea.setEditable(false);
        JScrollPane assistantScroll = new JScrollPane(assistantArea);
        assistantScroll.setBounds(280, 110, 200, 400);
        frame.add(assistantScroll);

        // --- CREATE NEW USER FORM

        JLabel createTitle = new JLabel("Create New User:");
        createTitle.setFont(new Font("Arial", Font.BOLD, 18));
        createTitle.setBounds(550, 80, 200, 30);
        frame.add(createTitle);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(550, 130, 80, 25);
        frame.add(nameLabel);
        JTextField nameField = new JTextField();
        nameField.setBounds(550, 155, 200, 25);
        frame.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(550, 190, 80, 25);
        frame.add(emailLabel);
        JTextField emailField = new JTextField();
        emailField.setBounds(550, 215, 200, 25);
        frame.add(emailField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(550, 250, 80, 25);
        frame.add(passLabel);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(550, 275, 200, 25);
        frame.add(passField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(550, 310, 80, 25);
        frame.add(roleLabel);
        // We only let Admins create Students and Assistants
        Role[] roles = {Role.STUDENT, Role.ASSISTANT};
        JComboBox<Role> roleDropdown = new JComboBox<>(roles);
        roleDropdown.setBounds(550, 335, 200, 25);
        frame.add(roleDropdown);

        JButton createUserButton = new JButton("Save User");
        createUserButton.setBounds(550, 380, 200, 35);
        createUserButton.setBackground(new Color(0x061757));
        createUserButton.setForeground(Color.WHITE);
        frame.add(createUserButton);

        // --- Button Action Listener (Database Save)
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passField.getPassword());
                Role selectedRole = (Role) roleDropdown.getSelectedItem();

                // Quick validation to make sure fields aren't empty
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields!");
                    return;
                }

                // Create the correct User Object
                user.User newUser = null;
                if (selectedRole == Role.STUDENT) {
                    newUser = new user.Student(name, email, password, selectedRole);
                } else if (selectedRole == Role.ASSISTANT) {
                    // Note: Giving a default limit of 5
                    newUser = new user.Assistant(name, email, password, selectedRole, 5);
                }

                // Save to Database
                Session session = factory.openSession();
                try {
                    session.beginTransaction();
                    session.persist(newUser); // The user is now in the database.
                    session.getTransaction().commit();

                    JOptionPane.showMessageDialog(frame, name + " was added successfully!");

                    // Refresh the window so the new user shows up in the lists!
                    frame.dispose();
                    new AdminDashboard(loggedInAdmin, factory);

                } catch (Exception ex) {
                    if (session.getTransaction() != null) session.getTransaction().rollback();   //if we started doing database changes but something crashed, throw everything away
                    JOptionPane.showMessageDialog(frame, "Error: Something went wrong.");
                    ex.printStackTrace();   //prints the red error text to the intellij console
                } finally {
                    session.close();
                }
            }
        });

        frame.setVisible(true);
    }
}