
import java.sql.*;
import java.util.Scanner;

public class RoomManagement {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/hostel_management";
        String user = "postgres";
        String password = System.getenv("DB_PASSWORD");

        Scanner sc = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            while (true) {
                System.out.println("\n--- ROOM MANAGEMENT ---");
                System.out.println("1. View Room Availability");
                System.out.println("2. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        viewRooms(con);
                        break;

                    case 2:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void viewRooms(Connection con) throws SQLException {

        String sql = "SELECT * FROM room_occupancy_report ORDER BY room_id";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- ROOM AVAILABILITY ---");

            while (rs.next()) {
                System.out.println(
                    "Hostel: " + rs.getString("hostel_name") +
                    " | Room: " + rs.getString("room_number") +
                    " | Capacity: " + rs.getInt("capacity") +
                    " | Occupied: " + rs.getLong("occupied_beds") +
                    " | Available: " + rs.getInt("available_beds")
                );
            }
        }
    }
}

