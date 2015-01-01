package shared;

import java.io.Serializable;

/**
 * User class.
 * This class encapsulates username and password from an user.
 *
 * Created by OmniBox on 02/11/14.
 */
public class User implements Serializable {
    final private String username;
    final private String password;
    final private static long serialVersionUID = 0101L;

    /**
     * User constructor.
     * Initialize final members variables.
     * @param username
     * @param password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns username.
     */
    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!password.equals(user.password)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        return result * 31;
    }
}
