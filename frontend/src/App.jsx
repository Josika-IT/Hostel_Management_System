
import { useState, useEffect } from "react";
import "./App.css";
const API = import.meta.env.VITE_API_URL;

const emptyForm = {
  studentName: "",
  department: "",
  year: "",
  contactNumber: "",
  roomId: "",
  totalFee: "",
  paidAmount: "0",
};

function App() {
  const [user, setUser] = useState(null);
  const [selectedRole, setSelectedRole] = useState("Admin");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loginError, setLoginError] = useState("");
  const [loginLoading, setLoginLoading] = useState(false);

  const [selectedHostel, setSelectedHostel] = useState("");
  const [activePage, setActivePage] = useState("Dashboard");

  const [dashboard, setDashboard] = useState(null);
  const [feeReport, setFeeReport] = useState([]);
  const [feeStudents, setFeeStudents] = useState([]);
  const [students, setStudents] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [checkedOutStudents, setCheckedOutStudents] = useState([]);
  const [allocations, setAllocations] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [actionMessage, setActionMessage] = useState("");
  const [actionError, setActionError] = useState("");

  const [showStudentForm, setShowStudentForm] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [studentForm, setStudentForm] = useState(emptyForm);
  const [savingStudent, setSavingStudent] = useState(false);

  const hostelType = selectedHostel.includes("Boys") ? "Boys" : "Girls";
  const query = `?hostel=${hostelType}`;

  const menuItems = [
    "Dashboard",
    "Students",
    "Checked-out Students",
    "Hostels & Rooms",
    "Room Allocation",
    ...(user?.role === "Admin" ? ["Fee Management"] : []),
    "Reports",
  ];

  const money = (amount) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(Number(amount) || 0);

  async function apiRequest(url, options = {}) {
    const response = await fetch(url, options);
    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      throw new Error(data.error || data.message || "Request failed.");
    }

    return data;
  }

  const loadHostelData = async () => {
    if (!selectedHostel) return;

    const currentHostel = selectedHostel.includes("Boys") ? "Boys" : "Girls";
    const currentQuery = `?hostel=${currentHostel}`;

    setLoading(true);
    setError("");

    try {
      const [
        dashboardData,
        feeData,
        studentData,
        allocationData,
        checkedOutData,
roomData,
] = await Promise.all([
        apiRequest(`${API}/api/dashboard${currentQuery}`),
        apiRequest(`${API}/api/fee-report${currentQuery}`),
        apiRequest(`${API}/api/students${currentQuery}`),
        apiRequest(`${API}/api/allocations${currentQuery}`),
        apiRequest(`${API}/api/checked-out${currentQuery}`),
        apiRequest(`${API}/api/rooms`),
      ]);

      setDashboard(dashboardData);
      setFeeReport(feeData.report || []);
      setFeeStudents(feeData.students || []);
      setStudents(studentData.students || []);
      setAllocations(allocationData.allocations || []);
      setCheckedOutStudents(checkedOutData.students || []);
      setRooms(roomData.rooms || []);
    } catch (err) {
      console.error("Data loading error:", err);
      setError(err.message || "Unable to load hostel data.");
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoginError("");
    setLoginLoading(true);

    try {
      const data = await apiRequest(`${API}/api/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username,
          password,
          role: selectedRole,
        }),
      });

      if (!data.success) {
        throw new Error(data.message || "Invalid username or password.");
      }

      setUser(data);
      setSelectedHostel("");
      setActivePage("Dashboard");
      setPassword("");
    } catch (err) {
      setLoginError(err.message || "Unable to connect to the server.");
    } finally {
      setLoginLoading(false);
    }
  };

  useEffect(() => {
    if (user && selectedHostel) {
      loadHostelData();
    }
  }, [user, selectedHostel]);

  const handleLogout = () => {
    setUser(null);
    setUsername("");
    setPassword("");
    setLoginError("");
    setSelectedHostel("");
    setDashboard(null);
    setFeeReport([]);
    setFeeStudents([]);
    setStudents([]);
    setCheckedOutStudents([]);
    setAllocations([]);
    setActivePage("Dashboard");
    setError("");
    setActionMessage("");
    setActionError("");
  };

  const openAddForm = () => {
    setEditingStudent(null);
    setStudentForm(emptyForm);
    setShowStudentForm(true);
    setActionMessage("");
    setActionError("");
  };

  const openEditForm = (student) => {
    setEditingStudent(student);
    setStudentForm({
      studentName: student.studentName || "",
      department: student.department || "",
      year: String(student.year || ""),
      contactNumber: student.contactNumber || "",
      roomId: String(student.roomId || ""),
      totalFee: "",
      paidAmount: "0",
    });
    setShowStudentForm(true);
    setActionMessage("");
    setActionError("");
  };

  const handleStudentFormChange = (e) => {
    const { name, value } = e.target;
    setStudentForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleStudentSubmit = async (e) => {
    e.preventDefault();
    setSavingStudent(true);
    setActionMessage("");
    setActionError("");

    try {
      if (editingStudent) {
        await apiRequest(`${API}/api/students`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            studentId: editingStudent.studentId,
            studentName: studentForm.studentName.trim(),
            department: studentForm.department.trim(),
            year: Number(studentForm.year),
            contactNumber: studentForm.contactNumber.trim(),
            roomId: Number(studentForm.roomId),
          }),
        });

        setActionMessage("Student details updated successfully.");
      } else {
        await apiRequest(`${API}/api/students`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            studentName: studentForm.studentName.trim(),
            department: studentForm.department.trim(),
            year: Number(studentForm.year),
            contactNumber: studentForm.contactNumber.trim(),
            roomId: Number(studentForm.roomId),
            totalFee: Number(studentForm.totalFee),
            paidAmount: Number(studentForm.paidAmount),
          }),
        });

        setActionMessage("New student added successfully.");
      }

      setShowStudentForm(false);
      setEditingStudent(null);
      setStudentForm(emptyForm);
      await loadHostelData();
    } catch (err) {
      setActionError(err.message || "Unable to save student.");
    } finally {
      setSavingStudent(false);
    }
  };

  const handleCheckout = async (student) => {
    const confirmed = window.confirm(
      `Are you sure you want to check out ${student.studentName}? Their details and history will be retained in Checked-out Students.`
    );

    if (!confirmed) return;

    setActionMessage("");
    setActionError("");

    try {
      await apiRequest(`${API}/api/checkout`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          studentId: student.studentId,
        }),
      });

      setActionMessage(
        `${student.studentName} has been checked out successfully.`
      );

      await loadHostelData();
    } catch (err) {
      setActionError(err.message || "Unable to check out student.");
    }
  };

  if (!user) {
    return (
      <div
        style={{
          minHeight: "100vh",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          background: "#f1f5f9",
          padding: "20px",
        }}
      >
        <div
          style={{
            width: "100%",
            maxWidth: "420px",
            background: "white",
            padding: "35px",
            borderRadius: "16px",
            boxShadow: "0 8px 30px rgba(0,0,0,0.08)",
          }}
        >
          <div style={{ textAlign: "center", marginBottom: "28px" }}>
            <div
              style={{
                width: "55px",
                height: "55px",
                margin: "0 auto 12px",
                borderRadius: "14px",
                background: "#2563eb",
                color: "white",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontSize: "28px",
                fontWeight: "bold",
              }}
            >
              H
            </div>
            <h1 style={{ margin: 0, color: "#1e293b" }}>HostelHub</h1>
            <p style={{ color: "#64748b", marginTop: "8px" }}>
              Hostel Management System
            </p>
          </div>

          <h3 style={{ marginBottom: "15px", color: "#334155" }}>
            Sign in to your account
          </h3>

          <form onSubmit={handleLogin}>
            <label>Select Role</label>
            <div style={{ display: "flex", gap: "10px", margin: "10px 0 22px" }}>
              {["Admin", "Warden"].map((role) => (
                <button
                  key={role}
                  type="button"
                  onClick={() => {
                    setSelectedRole(role);
                    setLoginError("");
                  }}
                  style={{
                    flex: 1,
                    padding: "12px",
                    borderRadius: "8px",
                    border:
                      selectedRole === role
                        ? "2px solid #2563eb"
                        : "1px solid #cbd5e1",
                    background: selectedRole === role ? "#eff6ff" : "white",
                    color: selectedRole === role ? "#2563eb" : "#475569",
                    fontWeight: "600",
                    cursor: "pointer",
                  }}
                >
                  {role}
                </button>
              ))}
            </div>

            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              required
              autoComplete="username"
              style={{
                width: "100%",
                padding: "12px",
                margin: "8px 0 18px",
                border: "1px solid #cbd5e1",
                borderRadius: "8px",
                boxSizing: "border-box",
              }}
            />

            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              required
              autoComplete="current-password"
              style={{
                width: "100%",
                padding: "12px",
                margin: "8px 0 18px",
                border: "1px solid #cbd5e1",
                borderRadius: "8px",
                boxSizing: "border-box",
              }}
            />

            {loginError && (
              <p style={{ color: "#dc2626", fontSize: "14px" }}>
                {loginError}
              </p>
            )}

            <button
              type="submit"
              disabled={loginLoading}
              style={{
                width: "100%",
                padding: "13px",
                background: "#2563eb",
                color: "white",
                border: "none",
                borderRadius: "8px",
                fontWeight: "600",
                fontSize: "16px",
                cursor: "pointer",
              }}
            >
              {loginLoading ? "Signing in..." : "Login"}
            </button>
          </form>
        </div>
      </div>
    );
  }

  if (!selectedHostel) {
    return (
      <div className="app">
        <aside className="sidebar">
          <div className="brand">
            <div className="brand-icon">H</div>
            <div>
              <h2>HostelHub</h2>
              <p>Management System</p>
            </div>
          </div>
          <div className="sidebar-bottom">
            <p>Hostel Management</p>
            <small>{user.role} Portal</small>
          </div>
        </aside>

        <main className="main-content">
          <header className="topbar">
            <div>
              <h2>Dashboard</h2>
              <p>Welcome to Hostel Management System</p>
            </div>
            <button onClick={handleLogout}>Logout</button>
          </header>

          <section className="welcome">
            <div>
              <h1>Welcome, {user.displayName}!</h1>
              <p>Select a hostel to manage its operations.</p>
            </div>
          </section>

          <section
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
              gap: "22px",
              marginTop: "25px",
            }}
          >
            {["Boys Hostel", "Girls Hostel"].map((hostel) => (
              <button
                key={hostel}
                onClick={() => {
                  setSelectedHostel(hostel);
                  setDashboard(null);
                }}
                style={{
                  padding: "35px 20px",
                  borderRadius: "14px",
                  border: "1px solid #e2e8f0",
                  background: "white",
                  cursor: "pointer",
                  textAlign: "center",
                  boxShadow: "0 4px 15px rgba(0,0,0,0.05)",
                }}
              >
                <div style={{ fontSize: "38px", marginBottom: "12px" }}>
                  🏢
                </div>
                <h2 style={{ color: "#1e293b", margin: "0 0 10px" }}>
                  {hostel}
                </h2>
                <p style={{ color: "#64748b", margin: 0 }}>
                  Select to manage {hostel.toLowerCase()}
                </p>
              </button>
            ))}
          </section>
        </main>
      </div>
    );
  }

  if (loading && !dashboard) {
    return (
      <div className="app">
        <main className="main-content">Loading hostel data...</main>
      </div>
    );
  }

  if (error && !dashboard) {
    return (
      <div className="app">
        <main className="main-content">
          <p>{error}</p>
          <button onClick={() => setSelectedHostel("")}>
            Back to Hostel Selection
          </button>
          <button onClick={loadHostelData}>Try Again</button>
        </main>
      </div>
    );
  }

  if (!dashboard) return null;

  const hostels = (dashboard.hostels || []).filter(
    (hostel) => hostel.totalBeds > 0
  );

  const inputStyle = {
    width: "100%",
    padding: "11px",
    border: "1px solid #cbd5e1",
    borderRadius: "7px",
    boxSizing: "border-box",
    marginTop: "6px",
  };

  const primaryButton = {
    padding: "10px 16px",
    background: "#2563eb",
    color: "white",
    border: "none",
    borderRadius: "7px",
    cursor: "pointer",
    fontWeight: "600",
  };

  const secondaryButton = {
    padding: "9px 14px",
    background: "white",
    color: "#334155",
    border: "1px solid #cbd5e1",
    borderRadius: "7px",
    cursor: "pointer",
  };

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">H</div>
          <div>
            <h2>HostelHub</h2>
            <p>Management System</p>
          </div>
        </div>

        <p className="menu-label">MAIN MENU</p>

        <nav>
          {menuItems.map((item) => (
            <button
              key={item}
              className={`menu-item ${activePage === item ? "active" : ""}`}
              onClick={() => {
                setActivePage(item);
                setShowStudentForm(false);
                setActionMessage("");
                setActionError("");
              }}
            >
              {item}
            </button>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <p>{selectedHostel}</p>
          <small>{user.role} Portal</small>
          <button
            onClick={() => {
              setSelectedHostel("");
              setActivePage("Dashboard");
            }}
            style={{ ...secondaryButton, marginTop: "12px", width: "100%" }}
          >
            Change Hostel
          </button>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div>
            <h2>{activePage}</h2>
            <p>{selectedHostel} Management</p>
          </div>

          <div className="admin-profile">
            <div className="avatar">
              {user.displayName?.charAt(0) || "U"}
            </div>
            <span>{user.displayName}</span>
            <button onClick={handleLogout} style={{ marginLeft: "15px" }}>
              Logout
            </button>
          </div>
        </header>

        {actionMessage && (
          <div
            style={{
              padding: "12px",
              margin: "15px 0",
              background: "#dcfce7",
              color: "#166534",
              borderRadius: "7px",
            }}
          >
            {actionMessage}
          </div>
        )}

        {actionError && (
          <div
            style={{
              padding: "12px",
              margin: "15px 0",
              background: "#fee2e2",
              color: "#991b1b",
              borderRadius: "7px",
            }}
          >
            {actionError}
          </div>
        )}

        {/* DASHBOARD */}
        {activePage === "Dashboard" && (
          <>
            <section className="welcome">
              <div>
                <h1>{selectedHostel} Dashboard</h1>
                <p>Manage hostel operations and student records.</p>
              </div>
              <span className="date-label">Live Dashboard</span>
            </section>

            <section className="stats-grid">
              <div className="stat-card">
                <p>Total Students</p>
                <h2>{dashboard.totalStudents}</h2>
              </div>
              <div className="stat-card">
                <p>Total Beds</p>
                <h2>{dashboard.totalBeds}</h2>
              </div>
              <div className="stat-card">
                <p>Occupied Beds</p>
                <h2>{dashboard.occupiedBeds}</h2>
              </div>
              <div className="stat-card">
                <p>Available Beds</p>
                <h2>{dashboard.availableBeds}</h2>
              </div>
            </section>
          </>
        )}

        {/* STUDENTS */}
        {activePage === "Students" && (
          <>
            <section className="welcome">
              <div>
                <h1>{selectedHostel} - Student Management</h1>
                <p>Add, view, edit and check out students.</p>
              </div>
              <button style={primaryButton} onClick={openAddForm}>
                + Add Student
              </button>
            </section>

            <section className="stats-grid">
              <div className="stat-card">
                <p>Active Students</p>
                <h2>{students.length}</h2>
              </div>
              <div className="stat-card">
                <p>Checked-out Students</p>
                <h2>{checkedOutStudents.length}</h2>
              </div>
            </section>

            {showStudentForm && (
              <section className="panel">
                <h3>
                  {editingStudent ? "Edit Student Details" : "Add New Student"}
                </h3>

                <form onSubmit={handleStudentSubmit}>
                  <div
                    style={{
                      display: "grid",
                      gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
                      gap: "16px",
                    }}
                  >
                    <label>
                      Student Name
                      <input
                        name="studentName"
                        value={studentForm.studentName}
                        onChange={handleStudentFormChange}
                        style={inputStyle}
                        required
                      />
                    </label>

                    <label>
                      Department
                      <input
                        name="department"
                        value={studentForm.department}
                        onChange={handleStudentFormChange}
                        style={inputStyle}
                        required
                      />
                    </label>

                    <label>
                      Year of Study
                      <select
                        name="year"
                        value={studentForm.year}
                        onChange={handleStudentFormChange}
                        style={inputStyle}
                        required
                      >
                        <option value="">Select year</option>
                        {[1, 2, 3, 4, 5, 6].map((year) => (
                          <option key={year} value={year}>
                            Year {year}
                          </option>
                        ))}
                      </select>
                    </label>

                    <label>
                      Contact Number
                      <input
                        name="contactNumber"
                        value={studentForm.contactNumber}
                        onChange={handleStudentFormChange}
                        style={inputStyle}
                        required
                      />
                    </label>
                    {editingStudent && (
    <label>
        Change Hostel & Room

        <select
            name="roomId"
            value={studentForm.roomId}
            onChange={handleStudentFormChange}
            style={inputStyle}
            required
        >
            <option value="">Select Hostel and Room</option>

            {rooms.map((room) => (
                <option
                    key={room.roomId}
                    value={room.roomId}
                    disabled={
                        room.availableBeds <= 0 &&
                        String(room.roomId) !== String(editingStudent.roomId)
                    }
                >
                    {room.hostelName} - Room {room.roomNumber}
                    {" "}({room.availableBeds} beds available)
                </option>
            ))}
        </select>
    </label>
)}
                    {!editingStudent && (
                      <>
                        <label>
                          Room ID
                          <input
                            name="roomId"
                            type="number"
                            min="1"
                            value={studentForm.roomId}
                            onChange={handleStudentFormChange}
                            style={inputStyle}
                            placeholder="Enter existing room ID"
                            required
                          />
                        </label>

                        <label>
                          Total Hostel Fee
                          <input
                            name="totalFee"
                            type="number"
                            min="0"
                            step="0.01"
                            value={studentForm.totalFee}
                            onChange={handleStudentFormChange}
                            style={inputStyle}
                            required
                          />
                        </label>

                        <label>
                          Amount Paid
                          <input
                            name="paidAmount"
                            type="number"
                            min="0"
                            step="0.01"
                            value={studentForm.paidAmount}
                            onChange={handleStudentFormChange}
                            style={inputStyle}
                            required
                          />
                        </label>
                      </>
                    )}
                  </div>

                  <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
                    <button
                      type="submit"
                      style={primaryButton}
                      disabled={savingStudent}
                    >
                      {savingStudent
                        ? "Saving..."
                        : editingStudent
                        ? "Update Student"
                        : "Save Student"}
                    </button>

                    <button
                      type="button"
                      style={secondaryButton}
                      onClick={() => {
                        setShowStudentForm(false);
                        setEditingStudent(null);
                      }}
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </section>
            )}

            <section className="panel">
              <h3>Active Registered Students</h3>

              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Student ID</th>
                      <th>Name</th>
                      <th>Department</th>
                      <th>Year</th>
                      <th>Contact Number</th>
                      <th>Room</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {students.map((student) => (
                      <tr key={student.studentId}>
                        <td>{student.studentId}</td>
                        <td>{student.studentName}</td>
                        <td>{student.department}</td>
                        <td>{student.year}</td>
                        <td>{student.contactNumber}</td>
                        <td>{student.roomNumber || "-"}</td>
                        <td>
                          <div style={{ display: "flex", gap: "7px" }}>
                            <button
                              style={secondaryButton}
                              onClick={() => openEditForm(student)}
                            >
                              Edit
                            </button>
                            <button
                              style={{
                                ...secondaryButton,
                                color: "#dc2626",
                                borderColor: "#fecaca",
                              }}
                              onClick={() => handleCheckout(student)}
                            >
                              Checkout
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}

                    {students.length === 0 && (
                      <tr>
                        <td colSpan="7">No active students found.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </section>
          </>
        )}

        {/* CHECKED-OUT STUDENTS */}
        {activePage === "Checked-out Students" && (
          <>
            <section className="welcome">
              <div>
                <h1>Checked-out Students</h1>
                <p>Previous students and their retained hostel records.</p>
              </div>
              <span className="date-label">{selectedHostel}</span>
            </section>

            <section className="stats-grid">
              <div className="stat-card">
                <p>Total Checked-out Students</p>
                <h2>{checkedOutStudents.length}</h2>
              </div>
            </section>

            <section className="panel">
              <h3>Student Checkout History</h3>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Student ID</th>
                      <th>Name</th>
                      <th>Department</th>
                      <th>Year</th>
                      <th>Contact Number</th>
                      <th>Hostel</th>
                      <th>Room</th>
                      <th>Checkout Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {checkedOutStudents.map((student) => (
                      <tr key={student.studentId}>
                        <td>{student.studentId}</td>
                        <td>{student.studentName}</td>
                        <td>{student.department}</td>
                        <td>{student.year}</td>
                        <td>{student.contactNumber}</td>
                        <td>{student.hostelName || student.hostelType || selectedHostel}</td>
                        <td>{student.roomNumber || "-"}</td>
                        <td>{student.checkOutDate || "-"}</td>
                      </tr>
                    ))}

                    {checkedOutStudents.length === 0 && (
                      <tr>
                        <td colSpan="8">No checked-out students found.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </section>
          </>
        )}

        {/* HOSTELS AND ROOMS */}
        {activePage === "Hostels & Rooms" && (
          <>
            <section className="welcome">
              <div>
                <h1>Hostel Occupancy</h1>
                <p>View capacity and availability of this hostel.</p>
              </div>
            </section>

            <section className="stats-grid">
              <div className="stat-card">
                <p>Total Hostels</p>
                <h2>{hostels.length}</h2>
              </div>
              <div className="stat-card">
                <p>Total Rooms</p>
                <h2>{dashboard.totalRooms}</h2>
              </div>
              <div className="stat-card">
                <p>Total Beds</p>
                <h2>{dashboard.totalBeds}</h2>
              </div>
              <div className="stat-card">
                <p>Available Beds</p>
                <h2>{dashboard.availableBeds}</h2>
              </div>
            </section>

            <section className="panel">
              <h3>Hostel Details</h3>
              {hostels.map((hostel, index) => {
                const available = hostel.totalBeds - hostel.occupiedBeds;
                const percentage =
                  hostel.totalBeds > 0
                    ? (hostel.occupiedBeds / hostel.totalBeds) * 100
                    : 0;

                return (
                  <div className="occupancy-row" key={index}>
                    <div className="occupancy-info">
                      <span>{hostel.name}</span>
                      <strong>
                        {hostel.occupiedBeds} / {hostel.totalBeds} occupied
                      </strong>
                    </div>
                    <div className="progress">
                      <div style={{ width: `${percentage}%` }}></div>
                    </div>
                    <p>{available} beds available</p>
                  </div>
                );
              })}
            </section>
          </>
        )}

        {/* ROOM ALLOCATION */}
        {activePage === "Room Allocation" && (
          <>
            <section className="welcome">
              <div>
                <h1>Room Allocation</h1>
                <p>View students and their assigned hostel rooms.</p>
              </div>
            </section>

            <section className="stats-grid">
              <div className="stat-card">
                <p>Active Allocations</p>
                <h2>{allocations.length}</h2>
              </div>
            </section>

            <section className="panel">
              <h3>Allocation Details</h3>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Student ID</th>
                      <th>Student Name</th>
                      <th>Hostel</th>
                      <th>Room Number</th>
                      <th>Check-in Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {allocations.map((item) => (
                      <tr key={item.studentId}>
                        <td>{item.studentId}</td>
                        <td>{item.studentName}</td>
                        <td>{item.hostelName}</td>
                        <td>{item.roomNumber}</td>
                        <td>{item.checkInDate}</td>
                      </tr>
                    ))}
                    {allocations.length === 0 && (
                      <tr>
                        <td colSpan="5">No active allocations found.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </section>
          </>
        )}

        {/* FEE MANAGEMENT */}
        {activePage === "Fee Management" && user.role === "Admin" && (
          <>
            <section className="welcome">
              <div>
                <h1>Fee Management</h1>
                <p>Monitor hostel fee collection and pending payments.</p>
              </div>
            </section>

            <section className="stats-grid">
              <div className="stat-card">
                <p>Total Fees</p>
                <h2>{money(dashboard.totalFees)}</h2>
              </div>
              <div className="stat-card">
                <p>Collected Fees</p>
                <h2>{money(dashboard.collectedFees)}</h2>
              </div>
              <div className="stat-card">
                <p>Pending Fees</p>
                <h2>{money(dashboard.pendingFees)}</h2>
              </div>
              <div className="stat-card">
                <p>Collection Rate</p>
                <h2>
                  {dashboard.totalFees > 0
                    ? Math.round(
                        (dashboard.collectedFees / dashboard.totalFees) * 100
                      )
                    : 0}
                  %
                </h2>
              </div>
            </section>

            <section className="panel">
              <h3>Hostel-wise Fee Pending Report</h3>
              <p>Student payment status for {selectedHostel}.</p>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Hostel</th>
                      <th>Total Students</th>
                      <th>Paid</th>
                      <th>Partially Paid</th>
                      <th>Unpaid</th>
                    </tr>
                  </thead>
                  <tbody>
                    {feeReport.map((item) => (
                      <tr key={item.hostelType}>
                        <td>{item.hostelType} Hostel</td>
                        <td>{item.totalStudents}</td>
                        <td>{item.paid}</td>
                        <td>{item.partial}</td>
                        <td>{item.unpaid}</td>
                      </tr>
                    ))}
                    {feeReport.length === 0 && (
                      <tr>
                        <td colSpan="5">No fee records found.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </section>

            <section className="panel">
              <h3>Individual Student Fee Details</h3>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Student ID</th>
                      <th>Name</th>
                      <th>Total Fee</th>
                      <th>Paid Amount</th>
                      <th>Pending Amount</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {feeStudents.map((student) => (
                      <tr key={student.studentId}>
                        <td>{student.studentId}</td>
                        <td>{student.studentName}</td>
                        <td>{money(student.totalAmount)}</td>
                        <td>{money(student.paidAmount)}</td>
                        <td>{money(student.pendingAmount)}</td>
                        <td>{student.paymentStatus}</td>
                      </tr>
                    ))}
                    {feeStudents.length === 0 && (
                      <tr>
                        <td colSpan="6">No individual fee records found.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </section>
          </>
        )}

        {/* REPORTS */}
        {activePage === "Reports" && (
          <>
            <section className="welcome">
              <div>
                <h1>Reports & Analytics</h1>
                <p>Overview of {selectedHostel} occupancy and fee collection.</p>
              </div>
            </section>

            <section className="stats-grid">
              <div className="stat-card">
                <p>Total Students</p>
                <h2>{dashboard.totalStudents}</h2>
              </div>
              <div className="stat-card">
                <p>Occupied Beds</p>
                <h2>{dashboard.occupiedBeds}</h2>
              </div>
              <div className="stat-card">
                <p>Available Beds</p>
                <h2>{dashboard.availableBeds}</h2>
              </div>
              {user.role === "Admin" && (
                <div className="stat-card">
                  <p>Pending Fees</p>
                  <h2>{money(dashboard.pendingFees)}</h2>
                </div>
              )}
            </section>

            <section className="panel">
              <h3>Hostel Occupancy Report</h3>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Hostel</th>
                      <th>Total Beds</th>
                      <th>Occupied</th>
                      <th>Available</th>
                    </tr>
                  </thead>
                  <tbody>
                    {hostels.map((hostel, index) => (
                      <tr key={index}>
                        <td>{hostel.name}</td>
                        <td>{hostel.totalBeds}</td>
                        <td>{hostel.occupiedBeds}</td>
                        <td>{hostel.totalBeds - hostel.occupiedBeds}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>

            {user.role === "Admin" && (
              <section className="panel">
                <h3>Fee Collection Report</h3>
                <div className="table-container">
                  <table>
                    <thead>
                      <tr>
                        <th>Hostel</th>
                        <th>Total Students</th>
                        <th>Paid</th>
                        <th>Partially Paid</th>
                        <th>Unpaid</th>
                      </tr>
                    </thead>
                    <tbody>
                      {feeReport.map((item) => (
                        <tr key={item.hostelType}>
                          <td>{item.hostelType} Hostel</td>
                          <td>{item.totalStudents}</td>
                          <td>{item.paid}</td>
                          <td>{item.partial}</td>
                          <td>{item.unpaid}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </section>
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default App;
