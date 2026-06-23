/*
 * Supervision Plan
 * Author: Viktoria Gospodinova
 * Last Change:  02.06.26
 */

package user;

import jakarta.persistence.*;

@Entity
public class Student extends User{


    @OneToOne
    @JoinColumn(name = "project_id")
    private topic.Topic project;

    @OneToOne
    @JoinColumn(name = "bach_thesis_id")
    private topic.Topic bachThesis;

    @OneToOne
    @JoinColumn(name = "master_thesis_id")
    private topic.Topic masterThesis;


    public Student() {
    }

    public Student(String name, String email, String password, Role role) {
        super(name, email, password, role);
    }


    public void enrollInProject(topic.Topic newProject){
        if(this.project != null){
            System.out.println("Warning: You are already enrolled in a Project!");
            return;
        }
        this.project = newProject;
        System.out.println("You have successfully enrolled in " + newProject.getTitle() + ".");
    }

    public void enrollInBachelorThesis(topic.Topic newBT) {
        if(this.bachThesis != null){
            System.out.println("Warning: You have already chosen " + bachThesis.getTitle() + " as your Bachelor Thesis topic!");
            return;
        }
        if(this.project == null){
            System.out.println("Waring: You cannot enroll in a Bachelor Thesis until you complete a Project");
            return;
        }
        this.bachThesis = newBT;
        System.out.println("You have successfully enrolled in " + newBT.getTitle() + ".");
    }

    public void enrollInMasterThesis(topic.Topic newMT){
        if(this.masterThesis != null){
            System.out.println("Warning: You have already chosen " + masterThesis.getTitle() + " as your Master Thesis topic!");
            return;
        }
        if(this.bachThesis == null){
            System.out.println("Warning: You cannot enroll in a Master Thesis until you complete a Bachelor Thesis.");
            return;
        }
        this.masterThesis = newMT;
        System.out.println("You have successfully enrolled in " + newMT.getTitle() + ".");
    }



    public topic.Topic getProject() {
        return project;
    }

    public topic.Topic getBachThesis() {
        return bachThesis;
    }

    public topic.Topic getMasterThesis() {
        return masterThesis;
    }

    public void setProject(topic.Topic project) {
        this.project = project;
    }

    public void setBachThesis(topic.Topic bachThesis) {
        this.bachThesis = bachThesis;
    }

    public void setMasterThesis(topic.Topic masterThesis) {
        this.masterThesis = masterThesis;
    }
}