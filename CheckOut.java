
import java.sql.*;
import java.util.Scanner;

public class CheckOut {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/hostel_management";
        String user = "postgres";
        String password = System.getenv("DB_PASSWORD");

        Scanner sc = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            System.out.println("--- STUDENT CHECK-OUT ---");

            System.out.print("Enter Student ID: ");
            int studentId = sc.nextInt();

            String sql = "UPDATE allocations " +
                    "SET check_out_date = CURRENT_DATE " +
                    "WHERE student_id = ? AND check_out_date IS NULL";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, studentId);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("Student checked out successfully!");
                } else {
                    System.out.println("No active allocation found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Check-out failed!");
            e.printStackTrace();
        }
    }
}

