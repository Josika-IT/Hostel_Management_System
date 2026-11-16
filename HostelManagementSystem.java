
public class HostelManagementSystem {

    public static void main(String[] args) throws Exception {

        while (true) {

            System.out.println("\n================================");
            System.out.println("     HOSTEL MANAGEMENT SYSTEM");
            System.out.println("================================");

            System.out.println("1. Student Management");
System.out.println("2. Room Management");
System.out.println("3. Room Allocation");
System.out.println("4. Student Check-Out");
System.out.println("5. Fee Management");
System.out.println("6. Reports");
System.out.println("7. Exit");

            System.out.print("Enter your choice: ");

            int choice = System.in.read();

            // Consume the remaining characters on the input line
            while (System.in.available() > 0) {
                int ch = System.in.read();
                if (ch == '\n') break;
            }

            String module = "";

            switch (choice) {
    case '1': module = "StudentManagement"; break;
    case '2': module = "RoomManagement"; break;
    case '3': module = "RoomAllocation"; break;
    case '4': module = "CheckOut"; break;
    case '5': module = "FeeManagement"; break;
    case '6': module = "Reports"; break;
    case '7':
        System.out.println("Thank you for using Hostel Management System!");
        return;
    default:
        System.out.println("Invalid choice!");
        continue;
}

            ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp",
                System.getProperty("java.class.path"),
                module
            );

            pb.inheritIO().start().waitFor();
        }
    }
}

