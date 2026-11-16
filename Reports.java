
import java.sql.*;

public class Reports {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/hostel_management";
        String user = "postgres";
        String password = System.getenv("DB_PASSWORD");

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            System.out.println("\n===== HOSTEL OCCUPANCY REPORT =====");

            String roomSQL = "SELECT hostel_name, " +
                    "SUM(capacity) AS total_beds, " +
                    "SUM(occupied_beds) AS occupied, " +
                    "SUM(available_beds) AS available " +
                    "FROM room_occupancy_report " +
                    "GROUP BY hostel_name";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(roomSQL)) {

                while (rs.next()) {
                    System.out.println(
                        rs.getString("hostel_name") +
                        " | Total Beds: " + rs.getInt("total_beds") +
                        " | Occupied: " + rs.getLong("occupied") +
                        " | Available: " + rs.getLong("available")
                    );
                }
            }

            System.out.println("\n===== FEE COLLECTION REPORT =====");

            String feeSQL = "SELECT " +
                    "SUM(total_amount) AS total_fees, " +
                    "SUM(paid_amount) AS collected, " +
                    "SUM(total_amount - paid_amount) AS pending " +
                    "FROM fees";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(feeSQL)) {

                if (rs.next()) {
                    System.out.println("Total Fees: " + rs.getBigDecimal("total_fees"));
                    System.out.println("Collected: " + rs.getBigDecimal("collected"));
                    System.out.println("Pending: " + rs.getBigDecimal("pending"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

