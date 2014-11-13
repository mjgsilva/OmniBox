package shared;

import java.io.Serializable;

/**
 * Created by OmniBox on 02/11/14.
 */
public class User implements Serializable {
    final private String username;
    final private String password;
    final private static long serialVersionUID = 0101L;

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
