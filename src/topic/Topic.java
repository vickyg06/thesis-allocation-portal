/*
 * Supervision Plan
 * Author: Viktoria Gospodinova
 * Last Change:  02.06.26
 */

package topic;


import jakarta.persistence.*;
import user.Assistant;

@Entity
@Table(name = "topic")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    public enum Type {
        PROJECT, BACHELOR_THESIS, MASTER_THESIS
    }

    @Enumerated(EnumType.STRING)
    private Type type;

    // A single Assistant can supervise MANY topics.
    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private user.Assistant supervisor;

    public Topic(){}

    public Topic(String title, Type type) {
        this.title = title;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Assistant getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Assistant supervisor) {
        this.supervisor = supervisor;
    }
}
