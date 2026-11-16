
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:postgresql://localhost:5432/hostel_management";

    private static final String USER = "postgres";

    public static Connection getConnection() throws SQLException {
        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new SQLException("DB_PASSWORD environment variable is not set.");
        }

        return DriverManager.getConnection(URL, USER, password);
    }

    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}

