
# DATABASE DOCUMENTATION

## 1. Introduction

The Hostel Management System uses PostgreSQL as its relational database management system. It stores and manages student information, hostel details, room information, room allocations, and fee records.

## 2. Database Name

`hostel_management`

## 3. Tables Used

The database contains five main tables:

1. **Hostels** – Stores hostel name, type, and address.
2. **Students** – Stores student details such as name, department, year, and contact number.
3. **Rooms** – Stores room number, room type, capacity, and hostel reference.
4. **Allocations** – Stores student room allocation and check-in/check-out dates.
5. **Fees** – Stores total fees, paid amount, payment date, and payment status.

## 4. Database Relationships

- One hostel can contain multiple rooms.
- One student can have multiple allocation records over time.
- One room can have multiple allocation records over time.
- One student can have multiple fee records.

## 5. Database Features

- Primary key and foreign key constraints.
- Unique constraints to prevent duplicate contact numbers and room numbers within a hostel.
- Check constraints to validate room capacity and fee amounts.
- Triggers to prevent room over-allocation and duplicate active student allocations.
- Views to generate occupancy, fee, and allocation reports.



## 6. Important SQL Queries

### 6.1 View All Students
```sql
SELECT * FROM students;
```

### 6.2 View All Hostels
```sql
SELECT * FROM hostels;
```

### 6.3 View Room Details
```sql
SELECT * FROM rooms;
```

### 6.4 View Room Occupancy
```sql
SELECT * FROM room_occupancy_report;
```

### 6.5 View Student Fee Details
```sql
SELECT * FROM student_fee_report;
```

### 6.6 View Student Allocation Details
```sql
SELECT * FROM student_allocation_report;
```

### 6.7 Calculate Total Fee Collection
```sql
SELECT
    SUM(total_amount) AS total_fees,
    SUM(paid_amount) AS collected,
    SUM(total_amount - paid_amount) AS pending
FROM fees;
```

### 6.8 Find Available Rooms
```sql
SELECT *
FROM room_occupancy_report
WHERE occupied < capacity;
```

## 7. Conclusion

The PostgreSQL database provides reliable storage and retrieval of hostel information. Constraints, triggers, and views help maintain data accuracy and generate useful reports.
