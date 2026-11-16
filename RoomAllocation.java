
import java.sql.*;
import java.util.Scanner;

public class RoomAllocation {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/hostel_management";
        String user = "postgres";
        String password = System.getenv("DB_PASSWORD");

        Scanner sc = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            System.out.println("--- ROOM ALLOCATION ---");

            System.out.print("Enter Student ID: ");
            int studentId = sc.nextInt();

            System.out.print("Enter Room ID: ");
            int roomId = sc.nextInt();

            String sql = "INSERT INTO allocations " +
                    "(student_id, room_id, check_in_date) " +
                    "VALUES (?, ?, CURRENT_DATE)";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, studentId);
                ps.setInt(2, roomId);

                ps.executeUpdate();

                System.out.println("Room allocated successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Room allocation failed!");
            System.out.println(e.getMessage());
        }
    }
}

