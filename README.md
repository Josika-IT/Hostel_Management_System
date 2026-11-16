# Hostel Management System

## Project Overview

The Hostel Management System is a web-based, database-driven application developed to simplify and automate hostel administration activities.

The system allows administrators and wardens to manage student information, hostel rooms, room allocations, student check-outs, fee payments, and reports through an interactive web interface.

## Features

- Admin and Warden login authentication.
- Add, view, update, and delete student records.
- Manage boys' and girls' hostels.
- View room availability and occupancy.
- Allocate rooms to students.
- Manage student check-ins and check-outs.
- Record and track hostel fee payments.
- Generate occupancy and fee collection reports.
- Dashboard for hostel management.

## Technologies Used

| Technology | Purpose |
|---|---|
| React | Frontend |
| Java 17 | Backend |
| PostgreSQL 18 | Database |
| JDBC | Database connectivity |
| HTML, CSS, JavaScript | User interface |
| Python | Data analysis (if implemented) |
| Psycopg2 | Python database connectivity |
| Matplotlib | Data visualization |

## Project Modules

1. Authentication and Login
2. Student Management
3. Hostel Management
4. Room Management
5. Room Allocation
6. Student Check-Out
7. Fee Management
8. Report Generation
9. Data Visualization Using Python

## Database Details

**Database Name:** `hostel_management`

Main database tables:

- Hostels
- Students
- Rooms
- Allocations
- Fees

The database uses primary keys, foreign keys, constraints, and database views to maintain data integrity and generate reports.

## Project Structure

```text
Hostel_Management_System/
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── .env
│   └── package.json
│
├── ApiServer.java
├── DBConnection.java
├── lib/
│   └── PostgreSQL JDBC Driver
│
├── Documentation/
│   ├── SRS.md
│   ├── ERDiagram.md
│   ├── ClassDiagram.md
│   ├── UseCaseDiagram.md
│   ├── SystemArchitecture.md
│   ├── DFDDiagram.md
│   ├── DatabaseDocumentation.md
│   └── TestCases.md
│
└── README.md
```

*Additional Java files and Python files should be included according to the actual project structure.*

## How to Run the Project

### 1. Start PostgreSQL

Make sure PostgreSQL is running and the `hostel_management` database has been created.

### 2. Configure Environment Variables

Configure the required database and login credentials for the Java backend.

Create a `.env` file inside the frontend folder and configure:

```env
VITE_API_URL=http://localhost:8080
```

### 3. Start Java Backend

Compile the Java files with the PostgreSQL JDBC driver available in the `lib` folder.

```powershell
javac -cp ".;lib/*" *.java
```

Run the API server:

```powershell
java -cp ".;lib/*" ApiServer
```

### 4. Start React Frontend

Open a new terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open the local URL displayed by Vite in your browser.

### 5. Python Data Analysis

Python database retrieval and visualization scripts will be included in the project when implemented.

## Documentation

The Documentation folder contains the project SRS, design diagrams, database documentation, and testing documents.

## Future Enhancements

- Downloadable reports.
- Improved fee management.
- Additional analytical dashboards.
- Enhanced authentication and access control.

## Conclusion

The Hostel Management System provides an organized and efficient approach to hostel administration by integrating student management, room allocation, fee tracking, and reporting into a centralized web-based application.
