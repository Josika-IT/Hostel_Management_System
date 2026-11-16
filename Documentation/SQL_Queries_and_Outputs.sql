-- =====================================
-- HOSTEL MANAGEMENT SYSTEM
-- SQL QUERIES AND OUTPUTS
-- =====================================

-- 1. Display all students
SELECT * FROM students;

2	"Priya"	     "CSE"	                    1	"9876543211"
3	"Karthik"	 "ECE"	                    3	"9876543212"
4	"Divya"	     "IT"	                    2	"9876543213"
1	"Arun"	     "Information Technology"	2	"9876543210"
5	"Rahul"    	 "Information Technology"	4	"9876543214"
10	"Naveen"	 "IT"	                    2	"9999999998"
11	"Josika M"	 "IT"	                    1	"1241241522"
12	"harini"	 "ECE"	                    3	"4561237894"
13	"kavya"	     "EEE"	                    4	"1452367896"
14	"Arjun"	     "IT"	                    2	"7788994455"

-- 2. Display all hostels
SELECT * FROM hostels;

1	"Boys Hostel"	"Boys"	"Erode"
2	"Girls Hostel"	"Girls"	"Erode"
3	"Boys Hostel"	"Boys"	"Erode"
4	"Girls Hostel"	"Girls"	"Erode"

-- 3. Display all rooms
SELECT * FROM rooms;

1	1	"101"	"AC"	    3
2	1	"102"	"Non-AC"	4
3	1	"103"	"AC"	    2
4	2	"201"	"AC"	    3
5	2	"202"	"Non-AC"	4

-- 4. Display room occupancy
SELECT * FROM room_occupancy_report;

1	"101"	"Boys Hostel"	3	2	1
3	"103"	"Boys Hostel"	2	0	2
4	"201"	"Girls Hostel"	3	1	2
2	"102"	"Boys Hostel"	4	1	3
5	"202"	"Girls Hostel"	4	2	2

-- 5. Display student allocation details
SELECT * FROM student_allocation_report;

1	"Arun"	      "Information Technology"	"Boys Hostel"	"101"	"2026-11-11"	
2	"Priya"	      "CSE"	                    "Girls Hostel"	"201"	"2026-11-11"	
3	"Karthik"	  "ECE"	                    "Boys Hostel"	"102"	"2026-11-11"	
10	"Naveen"	  "IT"	                    "Boys Hostel"	"101"	"2026-11-13"	"2026-11-13"
4	"Divya"	      "IT"	                    "Girls Hostel"	"201"	"2026-11-11"	"2026-11-16"
11	"Josika M"	  "IT"	                    "Girls Hostel"	"202"	"2026-11-16"	
12	"harini"	  "ECE"	                    "Girls Hostel"	"202"	"2026-11-16"	
13	"kavya"	      "EEE"	                    "Girls Hostel"	"202"	"2026-11-16"	"2026-11-16"
14	"Arjun"	      "IT"	                    "Boys Hostel"	"101"	"2026-11-16"	
5	"Rahul"	      "Information Technology"	"Boys Hostel"	"101"	"2026-11-11"	"2026-11-16"

-- 6. Display student fee details
SELECT * FROM student_fee_report;

2	"Priya"	    45000.00	45000.00	0.00	    "Paid"
3	"Karthik"	50000.00	0.00	    50000.00	"Pending"
4	"Divya" 	45000.00	20000.00	25000.00	"Partial"
5	"Rahul"	    50000.00	50000.00	0.00	    "Paid"
1	"Arun"	    50000.00	30000.00	20000.00	"Partial"
11	"Josika M"	50000.00	20000.00	30000.00	"Partial"
12	"harini"	70000.00	50000.00	20000.00	"Partial"
13	"kavya"	    45000.00	20000.00	25000.00	"Partial"
14	"Arjun"	    45000.00	35000.00	10000.00	"Partial"

-- 7. Calculate total fees collected
SELECT SUM(paid_amount) AS total_collected
FROM fees;

270000.00

-- 8. Calculate pending fees
SELECT SUM(total_amount - COALESCE(paid_amount, 0))
       AS total_pending
FROM fees;

180000.00

-- 9. Find available rooms
SELECT *
FROM room_occupancy_report
WHERE available_beds > 0;

1	"101"	"Boys Hostel"	3	2	1
3	"103"	"Boys Hostel"	2	0	2
4	"201"	"Girls Hostel"	3	1	2
2	"102"	"Boys Hostel"	4	1	3
5	"202"	"Girls Hostel"	4	2	2