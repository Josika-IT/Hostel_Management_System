
import java.sql.*;
import java.util.Scanner;

public class StudentManagement {

    static Scanner sc = new Scanner(System.in);

    static String url = "jdbc:postgresql://localhost:5432/hostel_management";
    static String user = "postgres";
    static String password = System.getenv("DB_PASSWORD");

    public static void main(String[] args) {

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            while (true) {
                System.out.println("\n--- STUDENT MANAGEMENT ---");
                System.out.println("1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Exit");
                System.out.println("4. Update Student");
                System.out.println("5. Delete Student");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        addStudent(con);
                        break;

                    case 2:
                        viewStudents(con);
                        break;

                    case 3:
                        System.out.println("Exiting...");
                        return;
                    case 4:
                        updateStudent(con);
                        break;

                    case 5:
                        deleteStudent(con);
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addStudent(Connection con) throws SQLException {

        System.out.print("Enter student name: ");
        String name = sc.nextLine();

        System.out.print("Enter department: ");
        String department = sc.nextLine();

        System.out.print("Enter year of study: ");
        int year = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter contact number: ");
        String contact = sc.nextLine();

        String sql = "INSERT INTO students " +
                "(student_name, department, year_of_study, contact_number) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, department);
            ps.setInt(3, year);
            ps.setString(4, contact);

            ps.executeUpdate();
            System.out.println("Student added successfully!");
        }
    }

    static void viewStudents(Connection con) throws SQLException {

        String sql = "SELECT * FROM students ORDER BY student_id";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- STUDENT DETAILS ---");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("student_id") + " | " +
                    rs.getString("student_name") + " | " +
                    rs.getString("department") + " | " +
                    rs.getInt("year_of_study") + " | " +
                    rs.getString("contact_number")
                );
            }
        }
    }
    
static void updateStudent(Connection con) throws SQLException {

    System.out.print("Enter Student ID: ");
    int id = sc.nextInt();
    sc.nextLine();

    System.out.print("Enter New Department: ");
    String department = sc.nextLine();

    String sql = "UPDATE students SET department = ? WHERE student_id = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, department);
        ps.setInt(2, id);

        int rows = ps.executeUpdate();

        if (rows > 0)
            System.out.println("Student updated successfully!");
        else
            System.out.println("Student ID not found!");
    }
}

static void deleteStudent(Connection con) throws SQLException {

    System.out.print("Enter Student ID to delete: ");
    int id = sc.nextInt();

    String sql = "DELETE FROM students WHERE student_id = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, id);

        int rows = ps.executeUpdate();

        if (rows > 0)
            System.out.println("Student deleted successfully!");
        else
            System.out.println("Student ID not found!");
    }
}
}

