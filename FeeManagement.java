
import java.sql.*;
import java.util.Scanner;

public class FeeManagement {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/hostel_management";
        String user = "postgres";
        String password = System.getenv("DB_PASSWORD");

        Scanner sc = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            while (true) {
                System.out.println("\n--- FEE MANAGEMENT ---");
                System.out.println("1. View Fee Details");
                System.out.println("2. Record Fee Payment");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        viewFees(con);
                        break;

                    case 2:
                        recordPayment(con, sc);
                        break;

                    case 3:
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void viewFees(Connection con) throws SQLException {

        String sql = "SELECT * FROM student_fee_report ORDER BY student_id";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("student_id") + " | " +
                    rs.getString("student_name") +
                    " | Total: " + rs.getBigDecimal("total_amount") +
                    " | Paid: " + rs.getBigDecimal("paid_amount") +
                    " | Pending: " + rs.getBigDecimal("pending_amount") +
                    " | Status: " + rs.getString("payment_status")
                );
            }
        }
    }

    static void recordPayment(Connection con, Scanner sc) throws SQLException {

        System.out.print("Enter Student ID: ");
        int studentId = sc.nextInt();

        System.out.print("Enter payment amount: ");
        double amount = sc.nextDouble();

        if (amount <= 0) {
            System.out.println("Enter a valid positive amount!");
            return;
        }

        String sql = "UPDATE fees SET paid_amount = paid_amount + ?, " +
                "payment_date = CURRENT_DATE, " +
                "payment_status = CASE " +
                "WHEN paid_amount + ? >= total_amount THEN 'Paid' " +
                "ELSE 'Partial' END " +
                "WHERE student_id = ? AND paid_amount + ? <= total_amount";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setDouble(2, amount);
            ps.setInt(3, studentId);
            ps.setDouble(4, amount);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Payment recorded successfully!");
            else
                System.out.println("Invalid student ID or payment exceeds pending fees!");
        }
    }
}

