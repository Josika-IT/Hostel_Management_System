# DATABASE NORMALIZATION
## Hostel Management System

### 1. Introduction
Database normalization is the process of organizing data into structured tables to reduce data redundancy and avoid insertion, deletion and update anomalies.

The Hostel Management System uses five main tables:
- HOSTELS
- STUDENTS
- ROOMS
- ALLOCATIONS
- FEES

### 2. First Normal Form (1NF)
A table is in First Normal Form when:
- Each column contains atomic (single) values.
- There are no repeating groups.
- Each record can be uniquely identified.

**Application:** All five tables store individual values in their columns. Each table has a primary key to identify its records.

### 3. Second Normal Form (2NF)
A table is in Second Normal Form when:
- It satisfies 1NF.
- Every non-key attribute depends on the whole candidate key.
- There are no partial dependencies.

**Application:** The tables use primary keys such as student_id, hostel_id, room_id, allocation_id and fee_id. Room numbers are also unique within each hostel through the combination of hostel_id and room_number.

### 4. Third Normal Form (3NF)
A table is in Third Normal Form when:
- It satisfies 2NF.
- There are no transitive dependencies between non-key attributes.

**Application:**
- Hostel information is stored separately in HOSTELS.
- Student information is maintained in STUDENTS.
- Room information is stored in ROOMS, linked to HOSTELS.
- Allocation details are maintained in ALLOCATIONS.
- Fee details are stored in FEES.

Foreign keys establish relationships between the tables and reduce unnecessary duplication of information.

### 5. Benefits of Normalization
1. Reduces data redundancy.
2. Improves data consistency.
3. Prevents insertion, deletion and update anomalies.
4. Maintains data integrity.
5. Makes database maintenance easier.

### 6. Conclusion
The Hostel Management System organizes its data into five related tables using primary keys and foreign keys. This structure supports database normalization and improves data consistency and maintainability.