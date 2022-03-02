import com.github.javafaker.Faker;

public class User {
    public final String email;
    public final String password;
    public final String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User getRandom() {
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.name().fullName();
        return new User(email, password, name);
    }

    public static User getRandomWithoutEmail() {
        Faker faker = new Faker();
        String password = faker.internet().password();
        String name = faker.name().fullName();
        return new User(null, password, name);
    }

    public static User getRandomWithoutPassword() {
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String name = faker.name().fullName();
        return new User(email, null, name);
    }

    public static User getRandomWithoutName() {
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        return new User(email, password, null);
    }
}