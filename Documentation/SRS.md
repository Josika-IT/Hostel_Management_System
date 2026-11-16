# SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

## Hostel Management System


**Project Type:** Comprehensive Assesment Activity  
**Technology Stack:** Java, PostgreSQL, Python, Matplotlib

---

## 1. Introduction

### 1.1 Purpose
The purpose of the Hostel Management System is to automate hostel administration activities such as student registration, room allocation, check-in, check-out, fee management and report generation.

The system reduces manual paperwork, minimizes allocation errors and provides accurate information about room availability and fee collection.

### 1.2 Scope
The Hostel Management System provides the following features:

- Student information management.
- Hostel and room management.
- Room allocation based on available capacity.
- Student check-in and check-out management.
- Hostel fee payment management.
- Room occupancy and availability reports.
- Fee collection and pending payment reports.
- Data analysis and graphical visualization using Python.

A web-based interface is planned to make these operations accessible through a browser.

### 1.3 Intended Users
- **Hostel Administrator:** Manages students, rooms, allocations and fees.
- **College Management:** Views hostel occupancy and fee reports.

---

## 2. Overall Description

### 2.1 Product Perspective
The Hostel Management System is a database-driven application developed using Java and PostgreSQL. Python is used to retrieve, analyze and visualize hostel data.

### 2.2 Product Functions
The system performs the following functions:

1. Add, view, update and delete student details.
2. Maintain hostel room information.
3. Allocate rooms to students.
4. Prevent room capacity overflow.
5. Prevent duplicate active room allocations.
6. Record student check-out.
7. View and update fee payment details.
8. Generate hostel occupancy reports.
9. Generate fee collection reports.
10. Display analytical graphs.

### 2.3 Operating Environment

- Operating System: Windows
- Programming Language: Java 17
- Database: PostgreSQL 18
- Data Analysis: Python 3.14
- Libraries: Psycopg2, Matplotlib
- Development Environment: Visual Studio Code

---

## 3. Functional Requirements

### FR1 – Student Management
The system shall allow the administrator to add, view, update and delete student records.

### FR2 – Room Management
The system shall maintain room numbers, hostel details, room types and room capacities.

### FR3 – Room Allocation
The system shall allocate available rooms to students and prevent allocation beyond the room capacity.

### FR4 – Check-In Management
The system shall record the check-in date when a student is allocated a room.

### FR5 – Check-Out Management
The system shall record the check-out date and update room availability.

### FR6 – Fee Management
The system shall maintain total fees, paid amounts, pending amounts and payment status.

### FR7 – Report Generation
The system shall generate reports for hostel occupancy and fee collection.

### FR8 – Data Visualization
The system shall use Python and Matplotlib to display hostel occupancy and fee collection graphs.

---

## 4. Non-Functional Requirements

### 4.1 Performance
The system should retrieve and display records efficiently.

### 4.2 Reliability
The system should maintain accurate records and prevent invalid room allocations and fee entries.

### 4.3 Security
Database access should require valid credentials. User access should be controlled appropriately.

### 4.4 Usability
The system should provide simple menus and clear output messages. The planned web interface should provide easy navigation.

### 4.5 Maintainability
The application should use separate modules for student, room, allocation, checkout, fee and reporting operations.

---

## 5. Hardware and Software Requirements

### Hardware Requirements
- Computer or laptop
- Minimum 4 GB RAM
- Keyboard and monitor
- Sufficient storage space

### Software Requirements
- Windows Operating System
- Java Development Kit 17
- PostgreSQL 18
- Python 3.14
- Visual Studio Code
- Matplotlib and Psycopg2 libraries

---

## 6. Database Requirements

The system uses PostgreSQL to store and manage the following entities:

- Hostels
- Students
- Rooms
- Allocations
- Fees

Primary keys and foreign keys are used to maintain relationships between records. Constraints and database triggers help maintain data integrity.

---

## 7. Expected Outcomes

The Hostel Management System is expected to:

- Reduce manual hostel administration work.
- Improve room allocation accuracy.
- Provide updated room availability.
- Simplify fee tracking.
- Generate accurate reports.
- Support data-driven hostel management through graphical analysis.

---

**End of Document**