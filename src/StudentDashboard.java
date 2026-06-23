/*
 * Supervision Plan
 * Author: Viktoria Gospodinova
 * Last Change:  02.06.26
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import topic.Topic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentDashboard {

    // The Constructor: This is where we catch the data passed from the Login screen!
    public StudentDashboard(user.Student loggedInStudent, SessionFactory factory) {

        // Set up the new window
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setTitle("Student Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(0xEAE0CF));
        frame.setLayout(null);

        //Set logo
        ImageIcon logo = new ImageIcon("logo.png");
        frame.setIconImage(logo.getImage());

        // Personalized Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInStudent.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setBounds(50, 30, 600, 40);
        frame.add(welcomeLabel);

        // Display their current status
        JLabel statusLabel = new JLabel("Your Current Enrollments:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setBounds(50, 100, 300, 30);
        frame.add(statusLabel);

        StringBuilder statusText = new StringBuilder();
        boolean hasTopic = false;

        if (loggedInStudent.getProject() != null) {
            statusText.append("Project: ").append(loggedInStudent.getProject().getTitle()).append("\n");
            hasTopic = true;
        }
        if (loggedInStudent.getBachThesis() != null) {
            statusText.append("Bachelor Thesis: ").append(loggedInStudent.getBachThesis().getTitle()).append("\n");
            hasTopic = true;
        }
        if (loggedInStudent.getMasterThesis() != null) {
            statusText.append("Master Thesis: ").append(loggedInStudent.getMasterThesis().getTitle()).append("\n");
            hasTopic = true;
        }
        if (!hasTopic) {
            statusText.append("You are not enrolled in any topics yet. Time to find one!");
        }

        // We use a JTextArea so it naturally handles the \n line breaks
        JTextArea displayArea = new JTextArea(statusText.toString());
        displayArea.setBounds(50, 140, 600, 100);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        displayArea.setOpaque(false);    // Make background transparent
        displayArea.setEditable(false);  // Stop user from typing in it
        displayArea.setFocusable(false); // Remove the blinking text cursor
        frame.add(displayArea);

        // Fetch Available Topics from Database
        List<topic.Topic> allTopics = null;
        Session session = factory.openSession();
        try {
            // "FROM Topic" automatically finds Projects, BachelorTheses, and MasterTheses!
            allTopics = session.createQuery("FROM Topic", topic.Topic.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }


        // The Enrollment Section
        JLabel availableLabel = new JLabel("Available Topics:");
        availableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        availableLabel.setBounds(50, 260, 300, 30);
        frame.add(availableLabel);

        // A Dropdown to hold the topics
        JComboBox<String> topicDropdown = new JComboBox<>();
        if (allTopics != null) {
            for (topic.Topic t : allTopics) {
                // We add the title and the specific type (Project, MasterThesis, etc.)
                topicDropdown.addItem(t.getTitle() + " (" + t.getType() + ")");
            }
        }
        topicDropdown.setBounds(50, 300, 450, 30);
        frame.add(topicDropdown);

        // The Action Button
        JButton enrollButton = new JButton("Apply");
        enrollButton.setBounds(520, 300, 120, 30);
        enrollButton.setBackground(new Color(0x061757));
        enrollButton.setForeground(Color.WHITE);
        enrollButton.setFocusPainted(false);
        frame.add(enrollButton);

        // The Button Action (Enrollment Logic) ---
        List<topic.Topic> finalAllTopics = allTopics;
        enrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Get the exact text they selected in the dropdown
                String selectedText = (String) topicDropdown.getSelectedItem();
                if (selectedText == null) return;

                // 2. Find the actual Topic object that matches that text
                topic.Topic selectedTopic = null;
                for (topic.Topic t : finalAllTopics) {
                    String dropdownFormat = t.getTitle() + " (" + t.getType() + ")";
                    if (dropdownFormat.equals(selectedText)) {
                        selectedTopic = t;
                        break;
                    }
                }

                if (selectedTopic == null) return;


                // The One Student Per Topic Check
                Session checkSession = factory.openSession();
                long takenCount = 0;
                try {
                    String query = "SELECT count(s) FROM Student s WHERE s.project.id = :topicId " +
                            "OR s.bachThesis.id = :topicId OR s.masterThesis.id = :topicId";

                    takenCount = checkSession.createQuery(query, Long.class)
                            .setParameter("topicId", selectedTopic.getId())
                            .uniqueResult();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    checkSession.close();
                }
                if (takenCount > 0) {
                    JOptionPane.showMessageDialog(frame, "Enrollment failed. Another student is already enrolled in this topic!", "Topic Unavailable", JOptionPane.WARNING_MESSAGE);
                    return;
                }



                //Checking what they have already signed up for
                boolean success = false;
                String errorMessage = "";

                if (selectedTopic.getType() == topic.Topic.Type.PROJECT) {
                    if (loggedInStudent.getProject() != null) {
                        errorMessage = "You are already enrolled in a Project!";
                    } else {
                        loggedInStudent.enrollInProject(selectedTopic);
                        success = true;
                    }
                } else if (selectedTopic.getType() == Topic.Type.BACHELOR_THESIS) {
                    if (loggedInStudent.getBachThesis() != null) {
                        errorMessage = "You already have a Bachelor Thesis!";
                    } else if (loggedInStudent.getProject() == null) {
                        errorMessage = "You must complete a Project before starting a Bachelor Thesis!";
                    } else {
                        loggedInStudent.enrollInBachelorThesis(selectedTopic);
                        success = true;
                    }
                } else if (selectedTopic.getType() == Topic.Type.MASTER_THESIS) {
                    if (loggedInStudent.getMasterThesis() != null) {
                        errorMessage = "You already have a Master Thesis!";
                    } else if (loggedInStudent.getBachThesis() == null) {
                        errorMessage = "You must complete a Bachelor Thesis before starting a Master Thesis!";
                    } else {
                        loggedInStudent.enrollInMasterThesis(selectedTopic);
                        success = true;
                    }
                }

                // If they failed the check, show an error message
                if (!success) {
                    JOptionPane.showMessageDialog(frame, errorMessage, "Enrollment Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Save to Database
                Session saveSession = factory.openSession();
                try {
                    saveSession.beginTransaction();

                    // merge() tells Hibernate: "This student already exists, just update their row with the new data!"
                    saveSession.merge(loggedInStudent);

                    saveSession.getTransaction().commit();

                    JOptionPane.showMessageDialog(frame, "Successfully enrolled in " + selectedTopic.getTitle() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh: Destroy this window and the updated one
                    frame.dispose();
                    new StudentDashboard(loggedInStudent, factory);

                } catch (Exception ex) {
                    if (saveSession.getTransaction() != null) {
                        saveSession.getTransaction().rollback();
                    }
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    saveSession.close();
                }
            }
        });

        frame.setVisible(true);
    }
}
