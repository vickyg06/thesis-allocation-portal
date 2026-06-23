/*
 * Supervision Plan
 * Author: Viktoria Gospodinova
 * Last Change:  02.06.26
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AssistantDashboard {


    public AssistantDashboard(user.Assistant loggedInAssistant, SessionFactory factory){

        JFrame frame = new JFrame();
        frame.setSize(800,600);
        frame.setTitle("Assistant Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(0xEAE0CF));
        frame.setLayout(null);

        //Logo
        ImageIcon logo = new ImageIcon("logo.png");
        frame.setIconImage(logo.getImage());

        //Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome "+ loggedInAssistant.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setBounds(50, 30, 600, 40);
        frame.add(welcomeLabel);

        //Current Topics
        JLabel currentTopics = new JLabel("Your Current Topics:");
        currentTopics.setFont(new Font("Arial", Font.BOLD, 18));
        currentTopics.setBounds(50, 100, 300, 30);
        frame.add(currentTopics);

        //ask the database for the assistant's topics
        Session session = factory.openSession();
        List<topic.Topic> myTopics = null;
        try{
            myTopics = session.createQuery("FROM Topic where supervisor.id = :supId", topic.Topic.class)
                    .setParameter("supId", loggedInAssistant.getId())
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        StringBuilder curTopicsText = new StringBuilder();
        boolean hasTopic = false;

        if(myTopics != null && !myTopics.isEmpty()){
            for(topic.Topic t : myTopics){
                curTopicsText.append(t.getTitle()).append(" (").append(t.getType()).append(")\n");
            }
        } else {
            curTopicsText.append("You are not supervising any topics yet.");
        }

        JTextArea displayArea = new JTextArea(curTopicsText.toString());
        displayArea.setBounds(50, 140, 600, 100);
        displayArea.setFont(new Font("Arial", Font.PLAIN, 16));
        displayArea.setOpaque(false);
        displayArea.setEditable(false);
        displayArea.setFocusable(false);
        frame.add(displayArea);


        //STUDENT LIMIT Update
        JLabel limitLabel = new JLabel("Update Supervision Limit:");
        limitLabel.setFont(new Font("Arial", Font.BOLD, 18));
        limitLabel.setBounds(50, 420, 300, 30);
        frame.add(limitLabel);

        JTextField limitField = new JTextField(String.valueOf(loggedInAssistant.getLimit()));
        limitField.setBounds(50, 450, 100, 30);
        frame.add(limitField);

        JButton updateLimitBtn = new JButton("Update Limit");
        updateLimitBtn.setBounds(160, 450, 130, 30);
        frame.add(updateLimitBtn);

        updateLimitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int newLimit = Integer.parseInt(limitField.getText());

                    loggedInAssistant.setLimit(newLimit);

                    Session updateSession = factory.openSession();
                    updateSession.beginTransaction();
                    updateSession.merge(loggedInAssistant); // Saves the new limit
                    updateSession.getTransaction().commit();
                    updateSession.close();

                    JOptionPane.showMessageDialog(frame, "Limit updated to " + newLimit + "!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
                }
            }
        });



        // TOPIC CREATION
        JLabel createLabel = new JLabel("Create a New Topic:");
        createLabel.setFont(new Font("Arial", Font.BOLD, 18));
        createLabel.setBounds(50, 290, 300, 30);
        frame.add(createLabel);
        // Dropdown menu for topic type
        String[] topicTypes = {"Project", "Bachelor Thesis", "Master Thesis"};
        JComboBox<String> typeDropdown = new JComboBox<>(topicTypes);
        typeDropdown.setBounds(50, 330, 150, 30);
        frame.add(typeDropdown);

        // The Text Box for the Title
        JTextField titleField = new JTextField();
        titleField.setBounds(210, 330, 300, 30);
        frame.add(titleField);

        // The Submit Button
        JButton createButton = new JButton("Confirm");
        createButton.setBounds(520, 330, 130, 30);
        createButton.setBackground(new Color(0x061757)); // Green!
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        frame.add(createButton);

        //The Button Action (Database Save)
        final List<topic.Topic> finalMyTopics = myTopics;
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) typeDropdown.getSelectedItem();
                String titleText = titleField.getText();

                //Don't let them submit an empty title
                if (titleText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a title for the topic!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Stop them if they have reached their topic limit
                if (finalMyTopics != null && finalMyTopics.size() >= loggedInAssistant.getLimit()) {
                    JOptionPane.showMessageDialog(frame,
                            "You cannot create more topics! Your limit is " + loggedInAssistant.getLimit(),
                            "Limit Reached",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Build the correct empty Topic object based on the dropdown
                topic.Topic newTopic = new topic.Topic();
                if (selectedType.equals("Project")) {
                    newTopic.setType(topic.Topic.Type.PROJECT);
                } else if (selectedType.equals("Bachelor Thesis")) {
                    newTopic.setType(topic.Topic.Type.BACHELOR_THESIS);
                } else if (selectedType.equals("Master Thesis")) {
                    newTopic.setType(topic.Topic.Type.MASTER_THESIS);
                }

                // Fill it with data
                newTopic.setTitle(titleText);

                //We attach the logged-in Assistant to the new topic
                newTopic.setSupervisor(loggedInAssistant);

                // Save to the Database
                Session saveSession = factory.openSession();
                try {
                    saveSession.beginTransaction();

                    // persist() is the command to insert a brand new row into the database
                    saveSession.persist(newTopic);

                    saveSession.getTransaction().commit();

                    JOptionPane.showMessageDialog(frame, "Successfully created: " + titleText, "Success", JOptionPane.INFORMATION_MESSAGE);

                    //Refresh: Destroy this window and instantly open a new one
                    frame.dispose();
                    new AssistantDashboard(loggedInAssistant, factory);

                } catch (Exception ex) {
                    if (saveSession.getTransaction() != null) saveSession.getTransaction().rollback();
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Database error. Could not save.", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    saveSession.close();
                }
            }
        });

        frame.setVisible(true);
    }


}
