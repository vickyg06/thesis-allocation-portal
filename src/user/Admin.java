/*
 * Supervision Plan
 * Author: Viktoria Gospodinova
 * Last Change:  02.06.26
 */

package user;

import jakarta.persistence.*;

@Entity
public class Admin extends User{
    public Admin(){}

    public Admin(String name, String email, String password, Role role){
        super(name, email, password, role);
    }
}
