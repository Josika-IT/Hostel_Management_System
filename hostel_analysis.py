import os
import psycopg2
import matplotlib.pyplot as plt

try:
    con = psycopg2.connect(
        host="localhost",
        database="hostel_management",
        user="postgres",
        password=os.environ["DB_PASSWORD"]
    )

    print("PostgreSQL Connected Successfully!")

    cursor = con.cursor()

    query = """
        SELECT hostel_name,
               SUM(occupied_beds),
               SUM(available_beds)
        FROM room_occupancy_report
        GROUP BY hostel_name
    """

    cursor.execute(query)
    data = cursor.fetchall()

    hostel_names = []
    occupied = []
    available = []

    for row in data:
        hostel_names.append(row[0])
        occupied.append(row[1])
        available.append(row[2])

    cursor.close()
    con.close()

    # Create occupancy chart
    plt.bar(hostel_names, occupied, label="Occupied Beds")
    plt.bar(hostel_names, available,
            bottom=occupied, label="Available Beds")

    plt.title("Hostel Occupancy Analysis")
    plt.xlabel("Hostel Name")
    plt.ylabel("Number of Beds")
    plt.legend()
    plt.savefig("hostel_occupancy.png", dpi=300)
    plt.show()
        # Fee Collection Analysis
    con = psycopg2.connect(
        host="localhost",
        database="hostel_management",
        user="postgres",
        password=os.environ["DB_PASSWORD"]
    )

    cursor = con.cursor()

    cursor.execute("""
        SELECT
            SUM(paid_amount),
            SUM(total_amount - paid_amount)
        FROM fees
    """)

    fee_data = cursor.fetchone()

    collected = float(fee_data[0])
    pending = float(fee_data[1])

    cursor.close()
    con.close()

    plt.figure()
    plt.pie(
        [collected, pending],
        labels=["Collected Fees", "Pending Fees"],
        autopct="%1.1f%%",
        startangle=90
    )

    plt.title("Hostel Fee Collection Analysis")
    plt.savefig("fee_collection.png", dpi=300)
    plt.show()

except Exception as e:
    print("Error:", e)

