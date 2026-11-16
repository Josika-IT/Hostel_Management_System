
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiServer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // LOGIN API
        server.createContext("/api/login", exchange -> {
            setCors(exchange);

            if (options(exchange)) return;

            if (!method(exchange, "POST")) {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String body = readBody(exchange);
                String username = getJsonField(body, "username");
                String password = getJsonField(body, "password");
                String role = getJsonField(body, "role");

                String adminPassword = System.getenv("ADMIN_PASSWORD");
                String wardenPassword = System.getenv("WARDEN_PASSWORD");

                if (adminPassword == null || adminPassword.isBlank()
                    || wardenPassword == null || wardenPassword.isBlank()) {
                    sendJson(exchange, 500,
                        "{\"error\":\"Login configuration is missing\"}");
                    return;
                }

                boolean valid = false;
                String displayName = "";

                if ("Admin".equalsIgnoreCase(role)
                    && "admin".equalsIgnoreCase(username)
                    && secureEquals(password, adminPassword)) {
                    valid = true;
                    displayName = "Administrator";

                } else if ("Warden".equalsIgnoreCase(role)
                    && "warden".equalsIgnoreCase(username)
                    && secureEquals(password, wardenPassword)) {
                    valid = true;
                    displayName = "Warden";
                }

                if (valid) {
                    sendJson(exchange, 200,
                        "{\"success\":true,"
                        + "\"role\":\"" + escapeJson(role) + "\","
                        + "\"username\":\"" + escapeJson(username) + "\","
                        + "\"displayName\":\"" + displayName + "\"}");
                } else {
                    sendJson(exchange, 401,
                        "{\"success\":false,"
                        + "\"message\":\"Invalid username or password\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, "{\"error\":\"Login failed\"}");
            }
        });

        // DASHBOARD API
        server.createContext("/api/dashboard", exchange -> {
            setCors(exchange);
            if (options(exchange)) return;

            try {
                sendJson(exchange, 200,
                    getDashboardData(getHostelType(exchange)));
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, error(e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, error("Dashboard database error"));
            }
        });

        // STUDENTS API
        // GET    /api/students?hostel=Boys
        // POST   /api/students
        // PUT    /api/students
        server.createContext("/api/students", exchange -> {
            setCors(exchange);
            if (options(exchange)) return;

            try {
                String requestMethod = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(requestMethod)) {
                    sendJson(exchange, 200,
                        getStudents(getHostelType(exchange)));

                } else if ("POST".equalsIgnoreCase(requestMethod)) {
                    sendJson(exchange, 201, addStudent(readBody(exchange)));

                } else if ("PUT".equalsIgnoreCase(requestMethod)) {
                    sendJson(exchange, 200, updateStudent(readBody(exchange)));

                } else {
                    sendJson(exchange, 405, error("Method not allowed"));
                }

            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, error(e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, error("Student operation failed"));
            }
        });
        // GET ALL ROOMS WITH AVAILABLE BED COUNTS
server.createContext("/api/rooms", exchange -> {
    setCors(exchange);
    if (options(exchange)) return;

    if (!method(exchange, "GET")) {
        sendJson(exchange, 405, error("Method not allowed"));
        return;
    }

    try {
        sendJson(exchange, 200, getRooms());
    } catch (Exception e) {
        e.printStackTrace();
        sendJson(exchange, 500, error("Unable to load rooms"));
    }
});

        // CHECKOUT API
        // POST /api/checkout
        server.createContext("/api/checkout", exchange -> {
            setCors(exchange);
            if (options(exchange)) return;

            if (!method(exchange, "POST")) {
                sendJson(exchange, 405, error("Method not allowed"));
                return;
            }

            try {
                sendJson(exchange, 200, checkoutStudent(readBody(exchange)));
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, error(e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, error("Checkout failed"));
            }
        });

        // CHECKED-OUT STUDENTS API
        server.createContext("/api/checked-out", exchange -> {
            setCors(exchange);
            if (options(exchange)) return;

            if (!method(exchange, "GET")) {
                sendJson(exchange, 405, error("Method not allowed"));
                return;
            }

            try {
                sendJson(exchange, 200,
                    getCheckedOutStudents(getHostelType(exchange)));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, error("Checkout history error"));
            }
        });

        // FEE REPORT API
        // Includes summary counts AND individual student fee details.
        server.createContext("/api/fee-report", exchange -> {
            setCors(exchange);
            if (options(exchange)) return;

            try {
                sendJson(exchange, 200,
                    getFeeReport(getHostelType(exchange)));
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, error(e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, error("Fee report error"));
            }
        });

        // ROOM ALLOCATION API
        server.createContext("/api/allocations", exchange -> {
            setCors(exchange);
            if (options(exchange)) return;

            try {
                sendJson(exchange, 200,
                    getAllocations(getHostelType(exchange)));
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, error(e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, error("Allocation report error"));
            }
        });

        server.start();

        System.out.println(
            "Hostel Management API running at http://localhost:8080"
        );
    }

    // COMMON HTTP HELPERS

    private static void setCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS"
        );
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers", "Content-Type, Authorization"
        );
    }

    private static boolean options(HttpExchange exchange)
        throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 204, "");
            return true;
        }
        return false;
    }

    private static boolean method(HttpExchange exchange, String expected) {
        return expected.equalsIgnoreCase(exchange.getRequestMethod());
    }

    private static String readBody(HttpExchange exchange)
        throws IOException {
        return new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );
    }

    private static void sendJson(
        HttpExchange exchange, int status, String json
    ) throws IOException {

        if (status == 204) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        byte[] response = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, response.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private static String error(String message) {
        return "{\"error\":\"" + escapeJson(message) + "\"}";
    }

    // JSON HELPERS

    private static String getJsonField(String json, String key) {
        Pattern pattern = Pattern.compile(
            "\"" + Pattern.quote(key)
            + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
        );

        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }

        return "";
    }

    private static String unescapeJson(String value) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);

                switch (next) {
                    case '"': result.append('"'); break;
                    case '\\': result.append('\\'); break;
                    case '/': result.append('/'); break;
                    case 'b': result.append('\b'); break;
                    case 'f': result.append('\f'); break;
                    case 'n': result.append('\n'); break;
                    case 'r': result.append('\r'); break;
                    case 't': result.append('\t'); break;
                    default: result.append(next);
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private static int getJsonInt(String json, String key) {
        String value = getJsonNumber(json, key);
        if (value.isBlank()) return 0;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + key);
        }
    }

    private static double getJsonDouble(String json, String key) {
        String value = getJsonNumber(json, key);
        if (value.isBlank()) return 0;

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + key);
        }
    }

    private static String getJsonNumber(String json, String key) {
        Pattern pattern = Pattern.compile(
            "\"" + Pattern.quote(key)
            + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)"
        );

        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static String escapeJson(String value) {
        if (value == null) return "";

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    private static boolean secureEquals(String input, String expected) {
        if (input == null || expected == null) return false;

        return MessageDigest.isEqual(
            input.getBytes(StandardCharsets.UTF_8),
            expected.getBytes(StandardCharsets.UTF_8)
        );
    }

    // HOSTEL FILTER

    private static String getHostelType(HttpExchange exchange) {

        String query = exchange.getRequestURI().getRawQuery();

        if (query == null || query.isBlank()) return null;

        for (String part : query.split("&")) {
            String[] pair = part.split("=", 2);

            if (pair.length == 2 && pair[0].equals("hostel")) {
                String hostel = URLDecoder.decode(
                    pair[1], StandardCharsets.UTF_8
                );

                if (hostel.equalsIgnoreCase("Boys")) return "Boys";
                if (hostel.equalsIgnoreCase("Girls")) return "Girls";

                throw new IllegalArgumentException("Invalid hostel type");
            }
        }

        return null;
    }

    // DASHBOARD

    private static String getDashboardData(String hostelType)
        throws SQLException {

        String sql = """
            WITH selected_hostels AS (
                SELECT hostel_id, hostel_name, hostel_type
                FROM hostels
                WHERE CAST(? AS TEXT) IS NULL
                   OR hostel_type = CAST(? AS TEXT)
            ),
            active_students AS (
                SELECT DISTINCT a.student_id
                FROM allocations a
                JOIN rooms r ON a.room_id = r.room_id
                JOIN selected_hostels h ON r.hostel_id = h.hostel_id
                WHERE a.check_out_date IS NULL
            ),
            fee_totals AS (
                SELECT student_id,
                       SUM(total_amount) AS total_amount,
                       SUM(paid_amount) AS paid_amount
                FROM fees
                GROUP BY student_id
            )
            SELECT
                (SELECT COUNT(*) FROM active_students) AS total_students,
                (SELECT COUNT(*) FROM selected_hostels) AS total_hostels,
                (SELECT COUNT(*)
                 FROM rooms r
                 JOIN selected_hostels h ON r.hostel_id = h.hostel_id
                ) AS total_rooms,
                (SELECT COALESCE(SUM(r.capacity), 0)
                 FROM rooms r
                 JOIN selected_hostels h ON r.hostel_id = h.hostel_id
                ) AS total_beds,
                (SELECT COUNT(*)
                 FROM allocations a
                 JOIN rooms r ON a.room_id = r.room_id
                 JOIN selected_hostels h ON r.hostel_id = h.hostel_id
                 WHERE a.check_out_date IS NULL
                ) AS occupied_beds,
                (SELECT COALESCE(SUM(f.total_amount), 0)
                 FROM active_students s
                 JOIN fee_totals f ON f.student_id = s.student_id
                ) AS total_fees,
                (SELECT COALESCE(SUM(f.paid_amount), 0)
                 FROM active_students s
                 JOIN fee_totals f ON f.student_id = s.student_id
                ) AS collected_fees
            """;

        int students, hostels, rooms, beds, occupied;
        double totalFees, collectedFees;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hostelType);
            ps.setString(2, hostelType);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                students = rs.getInt("total_students");
                hostels = rs.getInt("total_hostels");
                rooms = rs.getInt("total_rooms");
                beds = rs.getInt("total_beds");
                occupied = rs.getInt("occupied_beds");
                totalFees = rs.getDouble("total_fees");
                collectedFees = rs.getDouble("collected_fees");
            }
        }

        String hostelSql = """
            SELECT h.hostel_name,
                   COALESCE(SUM(r.capacity), 0) AS total_beds,
                   COUNT(a.allocation_id) AS occupied_beds
            FROM hostels h
            LEFT JOIN rooms r ON h.hostel_id = r.hostel_id
            LEFT JOIN allocations a
              ON r.room_id = a.room_id
             AND a.check_out_date IS NULL
            WHERE CAST(? AS TEXT) IS NULL
               OR h.hostel_type = CAST(? AS TEXT)
            GROUP BY h.hostel_id, h.hostel_name
            ORDER BY h.hostel_id
            """;

        StringBuilder hostelData = new StringBuilder("[");
        boolean first = true;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(hostelSql)) {

            ps.setString(1, hostelType);
            ps.setString(2, hostelType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (!first) hostelData.append(",");
                    first = false;

                    hostelData.append("{")
                        .append("\"name\":\"")
                        .append(escapeJson(rs.getString("hostel_name")))
                        .append("\",")
                        .append("\"totalBeds\":")
                        .append(rs.getInt("total_beds"))
                        .append(",")
                        .append("\"occupiedBeds\":")
                        .append(rs.getInt("occupied_beds"))
                        .append("}");
                }
            }
        }

        hostelData.append("]");

        return "{"
            + "\"totalStudents\":" + students + ","
            + "\"totalHostels\":" + hostels + ","
            + "\"totalRooms\":" + rooms + ","
            + "\"totalBeds\":" + beds + ","
            + "\"occupiedBeds\":" + occupied + ","
            + "\"availableBeds\":" + (beds - occupied) + ","
            + "\"totalFees\":" + totalFees + ","
            + "\"collectedFees\":" + collectedFees + ","
            + "\"pendingFees\":" + (totalFees - collectedFees) + ","
            + "\"hostels\":" + hostelData
            + "}";
    }

    // ADD STUDENT
    // Required JSON:
    // studentName, department, year, contactNumber,
    // roomId, totalFee, paidAmount

    private static String addStudent(String body)
        throws Exception {

        String name = getJsonField(body, "studentName").trim();
        String department = getJsonField(body, "department").trim();
        String contact = getJsonField(body, "contactNumber").trim();

        int year = getJsonInt(body, "year");
        int roomId = getJsonInt(body, "roomId");
        double totalFee = getJsonDouble(body, "totalFee");
        double paidAmount = getJsonDouble(body, "paidAmount");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Student name is required");
        }

        if (roomId <= 0) {
            throw new IllegalArgumentException("Select a valid room");
        }

        if (year < 1 || year > 6) {
            throw new IllegalArgumentException("Enter a valid study year");
        }

        if (totalFee < 0 || paidAmount < 0 || paidAmount > totalFee) {
            throw new IllegalArgumentException("Invalid fee amounts");
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                // Lock room while checking capacity.
                String roomSql = """
                    SELECT r.capacity, h.hostel_type
                    FROM rooms r
                    JOIN hostels h ON r.hostel_id = h.hostel_id
                    WHERE r.room_id = ?
                    FOR UPDATE OF r
                    """;

                int capacity;
                String hostelType;

                try (PreparedStatement ps = con.prepareStatement(roomSql)) {
                    ps.setInt(1, roomId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Room not found");
                        }

                        capacity = rs.getInt("capacity");
                        hostelType = rs.getString("hostel_type");
                    }
                }

                String countSql = """
                    SELECT COUNT(*)
                    FROM allocations
                    WHERE room_id = ?
                      AND check_out_date IS NULL
                    """;

                int occupied;

                try (PreparedStatement ps = con.prepareStatement(countSql)) {
                    ps.setInt(1, roomId);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        occupied = rs.getInt(1);
                    }
                }

                if (occupied >= capacity) {
                    throw new IllegalArgumentException("Room is full");
                }

                int studentId;

                String studentSql = """
                    INSERT INTO students
                    (student_name, department, year_of_study, contact_number)
                    VALUES (?, ?, ?, ?)
                    RETURNING student_id
                    """;

                try (PreparedStatement ps = con.prepareStatement(studentSql)) {
                    ps.setString(1, name);
                    ps.setString(2, department);
                    ps.setInt(3, year);
                    ps.setString(4, contact.isBlank() ? null : contact);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        studentId = rs.getInt("student_id");
                    }
                }

                String allocationSql = """
                    INSERT INTO allocations
                    (student_id, room_id, check_in_date)
                    VALUES (?, ?, CURRENT_DATE)
                    """;

                try (PreparedStatement ps = con.prepareStatement(allocationSql)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, roomId);
                    ps.executeUpdate();
                }

                String feeSql = """
                    INSERT INTO fees
                    (student_id, total_amount, paid_amount,
                     payment_date, payment_status)
                    VALUES (?, ?, ?, CURRENT_DATE, ?)
                    """;

                try (PreparedStatement ps = con.prepareStatement(feeSql)) {
                    ps.setInt(1, studentId);
                    ps.setDouble(2, totalFee);
                    ps.setDouble(3, paidAmount);
                    ps.setString(4, paymentStatus(totalFee, paidAmount));
                    ps.executeUpdate();
                }

                con.commit();

                return "{"
                    + "\"success\":true,"
                    + "\"studentId\":" + studentId + ","
                    + "\"hostelType\":\"" + escapeJson(hostelType) + "\","
                    + "\"message\":\"Student added successfully\""
                    + "}";

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    
    // EDIT STUDENT
    // Required JSON: studentId, studentName, department, year,
    // contactNumber, roomId

    private static String updateStudent(String body)
        throws Exception {

        int studentId = getJsonInt(body, "studentId");
        int roomId = getJsonInt(body, "roomId");

        String name = getJsonField(body, "studentName").trim();
        String department = getJsonField(body, "department").trim();
        String contact = getJsonField(body, "contactNumber").trim();
        int year = getJsonInt(body, "year");

        if (studentId <= 0 || roomId <= 0 || name.isBlank()) {
            throw new IllegalArgumentException(
                "Invalid student details or room"
            );
        }

        if (year < 1 || year > 6) {
            throw new IllegalArgumentException(
                "Enter a valid study year"
            );
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                // Check selected room and lock it
                int capacity;

                String roomSql = """
                    SELECT capacity
                    FROM rooms
                    WHERE room_id = ?
                    FOR UPDATE
                    """;

                try (PreparedStatement ps = con.prepareStatement(roomSql)) {
                    ps.setInt(1, roomId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                "Selected room not found"
                            );
                        }

                        capacity = rs.getInt("capacity");
                    }
                }

                // Find student's active allocation
                String activeSql = """
                    SELECT allocation_id
                    FROM allocations
                    WHERE student_id = ?
                      AND check_out_date IS NULL
                    FOR UPDATE
                    """;

                int allocationId;

                try (PreparedStatement ps = con.prepareStatement(activeSql)) {
                    ps.setInt(1, studentId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                "Active student allocation not found"
                            );
                        }

                        allocationId = rs.getInt("allocation_id");
                    }
                }

                // Count occupied beds, excluding this student
                String countSql = """
                    SELECT COUNT(*)
                    FROM allocations
                    WHERE room_id = ?
                      AND check_out_date IS NULL
                      AND student_id <> ?
                    """;

                int occupied;

                try (PreparedStatement ps = con.prepareStatement(countSql)) {
                    ps.setInt(1, roomId);
                    ps.setInt(2, studentId);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        occupied = rs.getInt(1);
                    }
                }

                if (occupied >= capacity) {
                    throw new IllegalArgumentException(
                        "Selected room is full"
                    );
                }

                // Update student details
                String studentSql = """
                    UPDATE students
                    SET student_name = ?,
                        department = ?,
                        year_of_study = ?,
                        contact_number = ?
                    WHERE student_id = ?
                    """;

                try (PreparedStatement ps = con.prepareStatement(studentSql)) {
                    ps.setString(1, name);
                    ps.setString(2, department);
                    ps.setInt(3, year);
                    ps.setString(4, contact.isBlank() ? null : contact);
                    ps.setInt(5, studentId);

                    if (ps.executeUpdate() != 1) {
                        throw new IllegalArgumentException(
                            "Student not found"
                        );
                    }
                }

                // Update room allocation
                String allocationSql = """
                    UPDATE allocations
                    SET room_id = ?
                    WHERE allocation_id = ?
                      AND check_out_date IS NULL
                    """;

                try (PreparedStatement ps = con.prepareStatement(allocationSql)) {
                    ps.setInt(1, roomId);
                    ps.setInt(2, allocationId);

                    if (ps.executeUpdate() != 1) {
                        throw new IllegalArgumentException(
                            "Unable to update room allocation"
                        );
                    }
                }

                con.commit();

                return "{\"success\":true,"
                    + "\"message\":\"Student and room updated successfully\"}";

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }


    // CHECKOUT STUDENT
    // Required JSON: studentId

    private static String checkoutStudent(String body)
        throws SQLException {

        int studentId = getJsonInt(body, "studentId");

        if (studentId <= 0) {
            throw new IllegalArgumentException("Invalid student ID");
        }

        String sql = """
            UPDATE allocations
            SET check_out_date = CURRENT_DATE
            WHERE student_id = ?
              AND check_out_date IS NULL
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            int updated = ps.executeUpdate();

            if (updated == 0) {
                throw new IllegalArgumentException(
                    "No active allocation found for this student"
                );
            }
        }

        return "{\"success\":true,\"message\":\"Student checked out successfully\"}";
    }

    // PAYMENT STATUS

    private static String paymentStatus(double total, double paid) {
        if (paid <= 0) return "Unpaid";
        if (paid >= total) return "Paid";
        return "Partially Paid";
    }

    // FEE REPORT
    // Returns summary counts and individual student fee details.

    private static String getFeeReport(String hostelType)
        throws SQLException {

        String sql = """
            WITH active_students AS (
                SELECT DISTINCT
                    s.student_id,
                    s.student_name,
                    s.department,
                    s.year_of_study,
                    h.hostel_type,
                    h.hostel_name,
                    r.room_number
                FROM students s
                JOIN allocations a ON s.student_id = a.student_id
                JOIN rooms r ON a.room_id = r.room_id
                JOIN hostels h ON r.hostel_id = h.hostel_id
                WHERE a.check_out_date IS NULL
                  AND (CAST(? AS TEXT) IS NULL
                       OR h.hostel_type = CAST(? AS TEXT))
            ),
            fee_totals AS (
                SELECT student_id,
                       SUM(total_amount) AS total_amount,
                       SUM(paid_amount) AS paid_amount
                FROM fees
                GROUP BY student_id
            )
            SELECT
                ast.student_id,
                ast.student_name,
                ast.department,
                ast.year_of_study,
                ast.hostel_type,
                ast.hostel_name,
                ast.room_number,
                COALESCE(f.total_amount, 0) AS total_amount,
                COALESCE(f.paid_amount, 0) AS paid_amount
            FROM active_students ast
            LEFT JOIN fee_totals f ON ast.student_id = f.student_id
            ORDER BY ast.hostel_type, ast.student_name
            """;

        StringBuilder report = new StringBuilder("[");
        StringBuilder students = new StringBuilder("[");
        boolean firstStudent = true;

        int boysTotal = 0, boysPaid = 0, boysPartial = 0, boysUnpaid = 0;
        int girlsTotal = 0, girlsPaid = 0, girlsPartial = 0, girlsUnpaid = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hostelType);
            ps.setString(2, hostelType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    String type = rs.getString("hostel_type");
                    String name = rs.getString("student_name");

                    double total = rs.getDouble("total_amount");
                    double paid = rs.getDouble("paid_amount");
                    double pending = total - paid;

                    String status = paymentStatus(total, paid);

                    if ("Boys".equalsIgnoreCase(type)) {
                        boysTotal++;
                        if ("Paid".equals(status)) boysPaid++;
                        else if ("Unpaid".equals(status)) boysUnpaid++;
                        else boysPartial++;
                    } else {
                        girlsTotal++;
                        if ("Paid".equals(status)) girlsPaid++;
                        else if ("Unpaid".equals(status)) girlsUnpaid++;
                        else girlsPartial++;
                    }

                    if (!firstStudent) students.append(",");
                    firstStudent = false;

                    students.append("{")
                        .append("\"studentId\":").append(rs.getInt("student_id")).append(",")
                        .append("\"studentName\":\"").append(escapeJson(name)).append("\",")
                        .append("\"department\":\"").append(escapeJson(rs.getString("department"))).append("\",")
                        .append("\"year\":").append(rs.getInt("year_of_study")).append(",")
                        .append("\"hostelType\":\"").append(escapeJson(type)).append("\",")
                        .append("\"hostelName\":\"").append(escapeJson(rs.getString("hostel_name"))).append("\",")
                        .append("\"roomNumber\":\"").append(escapeJson(rs.getString("room_number"))).append("\",")
                        .append("\"totalAmount\":").append(total).append(",")
                        .append("\"paidAmount\":").append(paid).append(",")
                        .append("\"pendingAmount\":").append(pending).append(",")
                        .append("\"paymentStatus\":\"").append(status).append("\"")
                        .append("}");
                }
            }
        }

        students.append("]");

        boolean first = true;

        if (boysTotal > 0) {
            report.append("{")
                .append("\"hostelType\":\"Boys\",")
                .append("\"totalStudents\":").append(boysTotal).append(",")
                .append("\"paid\":").append(boysPaid).append(",")
                .append("\"partial\":").append(boysPartial).append(",")
                .append("\"unpaid\":").append(boysUnpaid)
                .append("}");
            first = false;
        }

        if (girlsTotal > 0) {
            if (!first) report.append(",");

            report.append("{")
                .append("\"hostelType\":\"Girls\",")
                .append("\"totalStudents\":").append(girlsTotal).append(",")
                .append("\"paid\":").append(girlsPaid).append(",")
                .append("\"partial\":").append(girlsPartial).append(",")
                .append("\"unpaid\":").append(girlsUnpaid)
                .append("}");
        }

        report.append("]");

        return "{"
            + "\"report\":" + report + ","
            + "\"students\":" + students
            + "}";
    }

    // ACTIVE STUDENTS LIST

    private static String getStudents(String hostelType)
        throws SQLException {

        String sql = """
            SELECT DISTINCT
                s.student_id,
                s.student_name,
                s.department,
                s.year_of_study,
                s.contact_number,
                h.hostel_type,
                h.hostel_name,
                r.room_id,
                r.room_number
            FROM students s
            JOIN allocations a ON s.student_id = a.student_id
            JOIN rooms r ON a.room_id = r.room_id
            JOIN hostels h ON r.hostel_id = h.hostel_id
            WHERE a.check_out_date IS NULL
              AND (CAST(? AS TEXT) IS NULL
                   OR h.hostel_type = CAST(? AS TEXT))
            ORDER BY s.student_id
            """;

        StringBuilder json = new StringBuilder("{\"students\":[");
        boolean first = true;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hostelType);
            ps.setString(2, hostelType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;

                    json.append("{")
                        .append("\"studentId\":").append(rs.getInt("student_id")).append(",")
                        .append("\"studentName\":\"").append(escapeJson(rs.getString("student_name"))).append("\",")
                        .append("\"department\":\"").append(escapeJson(rs.getString("department"))).append("\",")
                        .append("\"year\":").append(rs.getInt("year_of_study")).append(",")
                        .append("\"contactNumber\":\"").append(escapeJson(rs.getString("contact_number"))).append("\",")
                        .append("\"hostelType\":\"").append(escapeJson(rs.getString("hostel_type"))).append("\",")
                        .append("\"hostelName\":\"").append(escapeJson(rs.getString("hostel_name"))).append("\",")
                        .append("\"roomId\":").append(rs.getInt("room_id")).append(",")
                        .append("\"roomNumber\":\"").append(escapeJson(rs.getString("room_number"))).append("\"")
                        .append("}");
                }
            }
        }

        json.append("]}");
        return json.toString();
    }

    // CHECKED-OUT STUDENTS
    // Keeps student details and displays the latest checkout.

    private static String getCheckedOutStudents(String hostelType)
        throws SQLException {

        String sql = """
            SELECT
                s.student_id,
                s.student_name,
                s.department,
                s.year_of_study,
                s.contact_number,
                h.hostel_type,
                h.hostel_name,
                r.room_number,
                a.check_out_date
            FROM students s
            JOIN allocations a ON s.student_id = a.student_id
            JOIN rooms r ON a.room_id = r.room_id
            JOIN hostels h ON r.hostel_id = h.hostel_id
            WHERE a.check_out_date IS NOT NULL
              AND NOT EXISTS (
                  SELECT 1
                  FROM allocations active
                  WHERE active.student_id = s.student_id
                    AND active.check_out_date IS NULL
              )
              AND a.allocation_id = (
                  SELECT MAX(a2.allocation_id)
                  FROM allocations a2
                  WHERE a2.student_id = s.student_id
                    AND a2.check_out_date IS NOT NULL
              )
              AND (CAST(? AS TEXT) IS NULL
                   OR h.hostel_type = CAST(? AS TEXT))
            ORDER BY a.check_out_date DESC
            """;

        StringBuilder json = new StringBuilder("{\"students\":[");
        boolean first = true;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hostelType);
            ps.setString(2, hostelType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;

                    json.append("{")
                        .append("\"studentId\":").append(rs.getInt("student_id")).append(",")
                        .append("\"studentName\":\"").append(escapeJson(rs.getString("student_name"))).append("\",")
                        .append("\"department\":\"").append(escapeJson(rs.getString("department"))).append("\",")
                        .append("\"year\":").append(rs.getInt("year_of_study")).append(",")
                        .append("\"contactNumber\":\"").append(escapeJson(rs.getString("contact_number"))).append("\",")
                        .append("\"hostelType\":\"").append(escapeJson(rs.getString("hostel_type"))).append("\",")
                        .append("\"hostelName\":\"").append(escapeJson(rs.getString("hostel_name"))).append("\",")
                        .append("\"roomNumber\":\"").append(escapeJson(rs.getString("room_number"))).append("\",")
                        .append("\"checkOutDate\":\"").append(rs.getDate("check_out_date")).append("\"")
                        .append("}");
                }
            }
        }

        json.append("]}");
        return json.toString();
    }

    // ACTIVE ROOM ALLOCATIONS

    private static String getAllocations(String hostelType)
        throws SQLException {

        String sql = """
            SELECT s.student_id, s.student_name,
                   h.hostel_name, h.hostel_type,
                   r.room_number, a.check_in_date
            FROM allocations a
            JOIN students s ON a.student_id = s.student_id
            JOIN rooms r ON a.room_id = r.room_id
            JOIN hostels h ON r.hostel_id = h.hostel_id
            WHERE a.check_out_date IS NULL
              AND (CAST(? AS TEXT) IS NULL
                   OR h.hostel_type = CAST(? AS TEXT))
            ORDER BY h.hostel_name, r.room_number
            """;

        StringBuilder json = new StringBuilder("{\"allocations\":[");
        boolean first = true;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hostelType);
            ps.setString(2, hostelType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;

                    json.append("{")
                        .append("\"studentId\":").append(rs.getInt("student_id")).append(",")
                        .append("\"studentName\":\"").append(escapeJson(rs.getString("student_name"))).append("\",")
                        .append("\"hostelName\":\"").append(escapeJson(rs.getString("hostel_name"))).append("\",")
                        .append("\"hostelType\":\"").append(escapeJson(rs.getString("hostel_type"))).append("\",")
                        .append("\"roomNumber\":\"").append(escapeJson(rs.getString("room_number"))).append("\",")
                        .append("\"checkInDate\":\"").append(rs.getDate("check_in_date")).append("\"")
                        .append("}");
                }
            }
        }

        json.append("]}");
        return json.toString();
    }
    private static String getRooms() throws SQLException {

    String sql = """
        SELECT
            r.room_id,
            r.room_number,
            r.capacity,
            h.hostel_name,
            h.hostel_type,
            r.capacity - COUNT(a.allocation_id) AS available_beds
        FROM rooms r
        JOIN hostels h ON r.hostel_id = h.hostel_id
        LEFT JOIN allocations a
            ON a.room_id = r.room_id
            AND a.check_out_date IS NULL
        GROUP BY
            r.room_id, r.room_number, r.capacity,
            h.hostel_name, h.hostel_type
        ORDER BY h.hostel_name, r.room_number
        """;

    StringBuilder json = new StringBuilder("{\"rooms\":[");
    boolean first = true;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            if (!first) json.append(",");
            first = false;

            json.append("{")
                .append("\"roomId\":").append(rs.getInt("room_id")).append(",")
                .append("\"roomNumber\":\"").append(escapeJson(rs.getString("room_number"))).append("\",")
                .append("\"capacity\":").append(rs.getInt("capacity")).append(",")
                .append("\"availableBeds\":").append(rs.getInt("available_beds")).append(",")
                .append("\"hostelName\":\"").append(escapeJson(rs.getString("hostel_name"))).append("\",")
                .append("\"hostelType\":\"").append(escapeJson(rs.getString("hostel_type"))).append("\"")
                .append("}");
        }
    }

    json.append("]}");
    return json.toString();
}
}
