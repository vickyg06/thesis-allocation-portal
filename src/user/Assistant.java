/*
 * Supervision Plan
 * Author: Viktoria Gospodinova
 * Last Change:  02.06.26
 */

package user;

import jakarta.persistence.*;

@Entity
public class Assistant extends User{

    @Column(name = "student_limit") //because just limit is an sql command and the database might get confused
    private int limit;


    public Assistant(){}

    public Assistant(String name, String email, String password, Role role, int limit){
        super(name, email, password, role);
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
