# SAMPLE INPUTS AND OUTPUTS
## Hostel Management System

### 1. Student Registration

**Sample Input**

| Field | Value |
|---|---|
| Student Name | Sample Student |
| Department | CSE |
| Year of Study | 2 |
| Contact Number | 9876501234 |

**Expected Output:** Student details are saved successfully and the student record appears in the student list.

### 2. Room Management

**Sample Input**

| Field | Value |
|---|---|
| Hostel | Girls Hostel |
| Room Number | 203 |
| Room Type | AC |
| Capacity | 3 |

**Expected Output:** Room details are saved and displayed in the room list.

### 3. Room Allocation

**Sample Input**

| Field | Value |
|---|---|
| Student ID | 15 (sample) |
| Room ID | 5 |
| Check-in Date | 2026-11-16 |

**Expected Output:** Student is allocated to the selected room if sufficient capacity is available.

### 4. Fee Management

**Sample Input**

| Field | Value |
|---|---|
| Student ID | 15 (sample) |
| Total Amount | ₹50,000 |
| Paid Amount | ₹20,000 |
| Payment Date | 2026-11-16 |

**Expected Output:** Payment details are recorded. Pending amount is ₹30,000 and payment status is Partial.

### 5. Student Checkout

**Sample Input**

| Field | Value |
|---|---|
| Student ID | 15 (sample) |
| Check-out Date | 2026-11-20 |

**Expected Output:** The allocation is updated with the checkout date and the room's available capacity increases.

### 6. Room Occupancy Report

**Sample Output**

| Room | Capacity | Occupied | Available |
|---|---:|---:|---:|
| 101 | 3 | 2 | 1 |
| 102 | 4 | 1 | 3 |
| 103 | 2 | 0 | 2 |
| 201 | 3 | 1 | 2 |
| 202 | 4 | 2 | 2 |

### 7. Fee Report

**Sample Output**

| Description | Amount |
|---|---:|
| Total Fees Collected | ₹2,70,000 |
| Total Pending Fees | ₹1,80,000 |

### 8. Conclusion

The sample inputs and outputs demonstrate the major operations of the Hostel Management System, including student registration, room management, room allocation, fee management, checkout and report generation.