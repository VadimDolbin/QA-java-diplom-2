public class UserUpdate {
    public final String email;
    public final String name;

    public UserUpdate(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static UserUpdate getUserUpdate(User user) {
        return new UserUpdate(user.email, user.name);
    }
}