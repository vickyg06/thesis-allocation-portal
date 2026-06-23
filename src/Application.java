/*
 * Supervision Plan
 * Author: Viktoria Gospodinova
 * Last Change:  02.06.26
 */

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import user.Admin;
import user.Assistant;
import user.Role;
import user.Student;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application {


    public static void main(String[] args) {
        //comment testDatabase() method call after first run of the program!
        //testDatabase();

        // Start the Database Engine; how GUI talks to database
        SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        JFrame frame = new JFrame();
        frame.setSize(700,500);
        frame.setTitle("Supervision Plan");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit upon close, instead of hiding
        frame.setResizable(false);
        frame.getContentPane().setBackground(new Color(0xEAE0CF));
        frame.setLayout(null); //we will manually place everything

        //set logo
        ImageIcon logo = new ImageIcon("logo.png");
        frame.setIconImage(logo.getImage());

        /*    HEADING     */

        ImageIcon image = new ImageIcon("imageV.png");
        java.awt.Image scaledImage = logo.getImage().getScaledInstance(80, 65, java.awt.Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setBounds(300, 35,80,65);
        frame.add(imageLabel);

        JLabel title = new JLabel("Welcome to the Thesis Allocation Portal!");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(165, 130, 400,30);
        frame.add(title);

        JLabel desc = new JLabel("Discover and apply for Bachelor's, " +
                "Master's and project topics.");
        desc.setBounds(170, 150, 400,50);
        frame.add(desc);


        /*   DRORDOWN MENU   */

        JLabel instruction = new JLabel("To get started, please select your role:");
        instruction.setBounds(230, 215, 300, 30);
        frame.add(instruction);

        JComboBox<user.Role> roleDropdown = new JComboBox<>(user.Role.values());
        roleDropdown.setBounds(270, 245, 140, 30);
        frame.add(roleDropdown);

        /*    LOG IN    */

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(200, 300, 60, 30);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        frame.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(270,300,200,30);
        frame.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(180, 340, 80, 30);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        frame.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(270, 340, 200, 30);
        frame.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(285, 395, 90, 30);
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setBackground(new Color(0x061757));
        loginButton.setForeground(Color.WHITE);
        frame.add(loginButton);

        /*  The Button Action   */
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                String email = emailField.getText();
                // password gets saved array of chars, so we make it a string
                String password = new String(passwordField.getPassword());
                user.Role selectedRole = (user.Role) roleDropdown.getSelectedItem();

                System.out.println("Attempting to connect to database...");

                //open session to MySQL
                Session session = factory.openSession();

                try {
                    user.User inputUser = session.createQuery("FROM User WHERE email = :userEmail", user.User.class)
                            .setParameter("userEmail", email)
                            .uniqueResult();


                    if(inputUser == null) {
                        JOptionPane.showMessageDialog(frame,"No account found with this email.");
                    } else if (!inputUser.getPassword().equals(password)) {
                        JOptionPane.showMessageDialog(frame,"Incorrect password!");
                    } else if (inputUser.getRole() != selectedRole) {
                        JOptionPane.showMessageDialog(frame,"You are not registered as " + selectedRole + "!");
                    } else {
                        JOptionPane.showMessageDialog(frame,"Welcome to the portal, " + inputUser.getName() + "!");

                        frame.dispose();

                        // Open the Dashboard
                        // We check if they are a student, then cast them to a Student object and pass them to the new window.
                        if (inputUser instanceof user.Student) {
                            new StudentDashboard((user.Student) inputUser, factory);
                        } else if(inputUser instanceof  user.Assistant){
                            new AssistantDashboard((user.Assistant) inputUser, factory);
                        } else if (inputUser instanceof user.Admin) {
                            new AdminDashboard((user.Admin) inputUser, factory);
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    session.close();
                }

            }
        });

        frame.setVisible(true);
    }







    //CREATING THREE INITIAL USERS
    public static void testDatabase() {
        System.out.println("Starting Database Engine...");

        // 1. Build the SessionFactory using your hibernate.cfg.xml file
        SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        // 2. Open the Session
        Session session = factory.openSession();

        try {
            // 3. Start the Transaction
            session.beginTransaction();

            //  CREATE OUR TEST DATA

            // Create some Users
            Admin boss = new Admin("Super Boss", "admin@uni.at", "1234", Role.ADMINISTRATOR);
            Assistant teacher = new Assistant("Dr. Smith", "smith@uni.at", "1234", Role.ASSISTANT, 5);
            Student max = new Student("Max Mustermann", "max@uni.at", "1234", Role.STUDENT);

            // Create a Topic
            topic.Topic aiProject = new topic.Topic();
            aiProject.setTitle("AI Machine Learning");
            aiProject.setType(topic.Topic.Type.PROJECT); // Using the Enum
            aiProject.setSupervisor(teacher);

            session.persist(teacher);

            max.enrollInProject(aiProject);

            session.persist(boss);
            session.persist(aiProject);

            session.persist(max);

            // 4. Commit the transaction (Lock it all in!)
            session.getTransaction().commit();
            System.out.println("TEST SUCCESSFUL: Data is locked in!");

        } catch (Exception e) {
            // If anything goes wrong, undo everything!
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            // 5. Close Session
            session.close();
            factory.close();
        }
    }



}

