package omniboxshared.shared;

import java.io.Serializable;

/**
 * Created by mario on 02/11/14.
 */
public class User implements Serializable {
    final private String username;
    final private String password;
    final private static long serialVersionUID = 01L;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
