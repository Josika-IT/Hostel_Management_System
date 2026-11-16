# Hostel Management System

## Project Overview
The Hostel Management System is a web-based application developed to simplify and automate hostel operations. It helps administrators and wardens manage student details, hostels, rooms, room allocations, check-outs, fee payments and reports.

## Technologies Used

### Frontend
- React
- Vite

### Backend
- Java 17
- JDBC
- Java HTTP Server

### Database
- PostgreSQL 18

### Data Analysis and Visualization
- Python 3.14
- Psycopg2
- Matplotlib

## Main Features
- Admin and Warden Login
- Student Management
- Hostel Management
- Room Management
- Room Allocation
- Check-Out Management
- Fee Management
- Room Availability and Occupancy Reports
- Student Fee Reports
- Data Visualization

## Project Structure

```text
Hostel_Management_System/
│
├── frontend/
├── Documentation/
├── visualizations/
├── lib/
├── ApiServer.java
├── DBConnection.java
├── StudentManagement.java
├── RoomManagement.java
├── RoomAllocation.java
├── FeeManagement.java
├── CheckOut.java
├── Reports.java
├── hostel_analysis.py
├── hostel_management.sql
├── README.md
└── .gitignore
```

## Database Setup
1. Install PostgreSQL.
2. Create a database named `hostel_management`.
3. Restore or execute the database SQL script.
4. Configure the database connection details.
5. Ensure the required tables, triggers and views are available.

## Environment Configuration

Configure these environment variables before starting the Java backend:

- `DB_PASSWORD`
- `ADMIN_PASSWORD`
- `WARDEN_PASSWORD`

Do not upload actual passwords or secret values to GitHub.

## Running the Frontend

Open a terminal inside the frontend directory:

```bash
npm install
npm run dev
```

## Running the Backend

Ensure Java 17 and the PostgreSQL JDBC driver are available.

Compile the Java source files and start the API server using the project's configured JDBC classpath.

The backend runs locally on port 8080.

## Running Python Visualization

Install the required packages:

```bash
pip install psycopg2-binary matplotlib
```

Configure the database password through the `DB_PASSWORD` environment variable, then run:

```bash
python hostel_analysis.py
```

The program generates:
- Hostel Occupancy Bar Chart
- Fee Collection Pie Chart

## Documentation
The Documentation folder contains:
- Software Requirements Specification (SRS)
- Use Case Diagram
- Class Diagram
- ER Diagram
- Data Flow Diagram
- System Architecture
- Database Normalization
- SQL Queries and Outputs
- Sample Inputs and Outputs
- Test Cases

## Project Objective
The main objective is to provide an efficient and organized hostel management solution while reducing manual work and improving data accuracy.

## Author
Hostel Management System – Academic Project