# 🎓 Student Portal Integration Guide

## For Student Portal Developer: Connect to Backend via WebSocket (Port 8003)

---

## 📋 Overview

### 🎯 Your Task:
Build a **Student Portal** where students can:
1. **Login using their Student ID**
2. **View their registered modules**
3. **Register for new modules**

### 🌐 Network Connection:
- **Protocol:** WebSocket (TCP-based, full-duplex communication)
- **Host:** `localhost`
- **Port:** `8003`
- **URL:** `ws://localhost:8003`
- **Message Format:** JSON

### ⚠️ Important:
- **NO REST APIs** - This uses WebSocket protocol for networking
- **NO HTTP requests** - Direct WebSocket communication only
- **Networking Concepts:** TCP sockets, WebSocket protocol, message-based communication

---

## 🔌 How to Connect to Backend

### Step 1: Create WebSocket Connection

```javascript
// Create WebSocket connection
const socket = new WebSocket('ws://localhost:8003');

// When connection opens
socket.onopen = function() {
    console.log('✅ Connected to backend on port 8003');
};

// When message received from backend
socket.onmessage = function(event) {
    const response = JSON.parse(event.data);
    console.log('📨 Received:', response);
    // Handle response here
};

// If connection error
socket.onerror = function(error) {
    console.error('❌ Connection error:', error);
};

// When connection closes
socket.onclose = function() {
    console.log('⚠️ Disconnected from backend');
};
```

### Step 2: Message Protocol

**All messages are JSON with this format:**

**When YOU send to backend:**
```json
{
  "type": "REQUEST",
  "action": "ACTION_NAME",
  "data": {
    // your data here
  }
}
```

**What backend sends back (Success):**
```json
{
  "type": "RESPONSE",
  "action": "ACTION_NAME",
  "data": {
    // response data here
  }
}
```

**What backend sends back (Error):**
```json
{
  "type": "ERROR",
  "action": "ACTION_NAME",
  "error": "Error message here"
}
```

---

## 🔑 Task 1: Student Login

### What you need to do:
1. Student enters their Student ID (e.g., "std1", "std2")
2. Send `GET_STUDENT` request to backend
3. If valid, backend returns student info
4. Store student data and show dashboard

### Network Flow:
```
Student enters ID → WebSocket → Backend checks → Response → Show dashboard
```

### Request to Send:

```javascript
{
  "type": "REQUEST",
  "action": "GET_STUDENT",
  "data": {
    "studentId": "std1"  // The ID student entered
  }
}
```

### Response You'll Get (Success):

```javascript
{
  "type": "RESPONSE",
  "action": "GET_STUDENT",
  "data": {
    "id": "std1",
    "name": "John Doe",
    "email": "john.doe@university.com",
    "batch": "CS2023A",
    "department": "Computer Science",
    "phone": "555-0101",
    "enrollmentDate": "2023-01-15"
  }
}
```

### Response You'll Get (Invalid ID):

```javascript
{
  "type": "ERROR",
  "action": "GET_STUDENT",
  "error": "Student not found"
}
```

### Complete Login Code Example:

```javascript
function loginStudent(studentId) {
    // Create request message
    const request = {
        type: "REQUEST",
        action: "GET_STUDENT",
        data: { studentId: studentId }
    };

    // Send request over WebSocket
    socket.send(JSON.stringify(request));

    // Listen for response
    socket.onmessage = function(event) {
        const response = JSON.parse(event.data);
        
        if (response.action === "GET_STUDENT") {
            if (response.type === "RESPONSE") {
                // Success! Student exists
                console.log("Student found:", response.data);
                
                // Save student data
                localStorage.setItem('student', JSON.stringify(response.data));
                
                // Go to dashboard
                window.location.href = 'dashboard.html';
            } 
            else if (response.type === "ERROR") {
                // Invalid student ID
                alert("Error: " + response.error);
            }
        }
    };
}

// Usage: When student submits login form
loginStudent("std1");
```

---

## 📚 Task 2: Get Student's Registered Modules

### What you need to do:
1. After login, send `GET_STUDENT_REGISTRATIONS` request
2. Backend returns list of modules student is registered in
3. Display them in a table or list

### Request to Send:

```javascript
{
  "type": "REQUEST",
  "action": "GET_STUDENT_REGISTRATIONS",
  "data": {
    "studentId": "std1"
  }
}
```

### Response You'll Get:

```javascript
{
  "type": "RESPONSE",
  "action": "GET_STUDENT_REGISTRATIONS",
  "data": [
    {
      "module": {
        "code": "CS101",
        "name": "Introduction to Programming",
        "credits": 3,
        "lecturer": "Dr. Smith",
        "semester": 1,
        "description": "Learn fundamentals of programming"
      },
      "registrationDate": "2023-09-15T10:30:00"
    },
    {
      "module": {
        "code": "MATH201",
        "name": "Calculus II",
        "credits": 4,
        "lecturer": "Prof. Johnson",
        "semester": 2,
        "description": "Advanced calculus topics"
      },
      "registrationDate": "2023-09-15T11:00:00"
    }
  ]
}
```

### Complete Code Example:

```javascript
function getStudentModules(studentId) {
    const request = {
        type: "REQUEST",
        action: "GET_STUDENT_REGISTRATIONS",
        data: { studentId: studentId }
    };

    socket.send(JSON.stringify(request));

    socket.onmessage = function(event) {
        const response = JSON.parse(event.data);
        
        if (response.action === "GET_STUDENT_REGISTRATIONS") {
            if (response.type === "RESPONSE") {
                // Success! Display modules
                displayModules(response.data);
            } else if (response.type === "ERROR") {
                alert("Error: " + response.error);
            }
        }
    };
}

function displayModules(registrations) {
    const tableBody = document.getElementById('modules-table');
    tableBody.innerHTML = ''; // Clear table

    registrations.forEach(reg => {
        const row = `
            <tr>
                <td>${reg.module.code}</td>
                <td>${reg.module.name}</td>
                <td>${reg.module.credits}</td>
                <td>${reg.module.lecturer}</td>
                <td>${reg.module.semester}</td>
                <td>${new Date(reg.registrationDate).toLocaleDateString()}</td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
}

// Usage: Load modules on dashboard
const student = JSON.parse(localStorage.getItem('student'));
getStudentModules(student.id);
```

---

## ➕ Task 3: Register for New Modules

### Part A: Get Available Modules

First, get list of modules student can register for:

### Request to Send:

```javascript
{
  "type": "REQUEST",
  "action": "GET_AVAILABLE_MODULES",
  "data": {
    "studentId": "std1"
  }
}
```

### Response You'll Get:

```javascript
{
  "type": "RESPONSE",
  "action": "GET_AVAILABLE_MODULES",
  "data": [
    {
      "code": "CS102",
      "name": "Data Structures",
      "credits": 4,
      "lecturer": "Dr. Williams",
      "semester": 2,
      "description": "Study of data structures and algorithms"
    },
    {
      "code": "ENG301",
      "name": "Software Engineering",
      "credits": 3,
      "lecturer": "Prof. Davis",
      "semester": 3,
      "description": "Software development methodologies"
    }
  ]
}
```

### Part B: Register for a Module

When student clicks "Register" on a module:

### Request to Send:

```javascript
{
  "type": "REQUEST",
  "action": "REGISTER_MODULE",
  "data": {
    "studentId": "std1",
    "moduleCode": "CS102"
  }
}
```

### Response You'll Get (Success):

```javascript
{
  "type": "RESPONSE",
  "action": "REGISTER_MODULE",
  "data": {
    "studentId": "std1",
    "moduleCode": "CS102",
    "registrationDate": "2023-09-20T14:25:00",
    "status": "SUCCESS"
  }
}
```

### Response You'll Get (Error):

```javascript
{
  "type": "ERROR",
  "action": "REGISTER_MODULE",
  "error": "Student already registered for this module"
}
```

### Complete Code Example:

```javascript
// Show available modules
function showAvailableModules(studentId) {
    const request = {
        type: "REQUEST",
        action: "GET_AVAILABLE_MODULES",
        data: { studentId: studentId }
    };

    socket.send(JSON.stringify(request));

    socket.onmessage = function(event) {
        const response = JSON.parse(event.data);
        
        if (response.action === "GET_AVAILABLE_MODULES") {
            if (response.type === "RESPONSE") {
                displayAvailableModules(response.data);
            }
        }
    };
}

function displayAvailableModules(modules) {
    const container = document.getElementById('available-modules');
    container.innerHTML = '';

    modules.forEach(module => {
        const card = `
            <div class="module-card">
                <h3>${module.code}: ${module.name}</h3>
                <p>Lecturer: ${module.lecturer}</p>
                <p>Credits: ${module.credits} | Semester: ${module.semester}</p>
                <p>${module.description}</p>
                <button onclick="registerForModule('${module.code}')">
                    Register
                </button>
            </div>
        `;
        container.innerHTML += card;
    });
}

// Register for a module
function registerForModule(moduleCode) {
    const student = JSON.parse(localStorage.getItem('student'));
    
    const request = {
        type: "REQUEST",
        action: "REGISTER_MODULE",
        data: {
            studentId: student.id,
            moduleCode: moduleCode
        }
    };

    socket.send(JSON.stringify(request));

    socket.onmessage = function(event) {
        const response = JSON.parse(event.data);
        
        if (response.action === "REGISTER_MODULE") {
            if (response.type === "RESPONSE") {
                alert("Successfully registered for " + moduleCode + "!");
                // Refresh available modules list
                showAvailableModules(student.id);
            } else if (response.type === "ERROR") {
                alert("Registration failed: " + response.error);
            }
        }
    };
}
```

---

## 📝 Complete Working Example

Here's a complete working HTML page you can start with:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Student Portal</title>
    <style>
        body { font-family: Arial; max-width: 800px; margin: 50px auto; }
        .section { margin: 30px 0; padding: 20px; border: 1px solid #ddd; }
        input { padding: 8px; margin: 10px 0; }
        button { padding: 10px 20px; background: #007bff; color: white; border: none; cursor: pointer; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background: #f5f5f5; }
        .status { padding: 10px; margin: 10px 0; }
        .connected { background: #d4edda; color: #155724; }
        .disconnected { background: #f8d7da; color: #721c24; }
    </style>
</head>
<body>
    <h1>🎓 Student Portal</h1>

    <!-- Connection Status -->
    <div id="status" class="status disconnected">Connecting to backend...</div>

    <!-- Login Section -->
    <div id="loginSection" class="section">
        <h2>Login</h2>
        <input type="text" id="studentId" placeholder="Enter Student ID (e.g., std1)" />
        <button onclick="login()">Login</button>
    </div>

    <!-- Dashboard Section (hidden until login) -->
    <div id="dashboardSection" class="section" style="display: none;">
        <h2>My Modules</h2>
        <p><strong>Student:</strong> <span id="studentName"></span></p>
        <table>
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Module Name</th>
                    <th>Credits</th>
                    <th>Lecturer</th>
                    <th>Semester</th>
                </tr>
            </thead>
            <tbody id="modulesTable"></tbody>
        </table>
        <button onclick="showRegistrationPage()">Register for New Modules</button>
    </div>

    <!-- Registration Section -->
    <div id="registrationSection" class="section" style="display: none;">
        <h2>Available Modules</h2>
        <div id="availableModules"></div>
        <button onclick="backToDashboard()">Back to Dashboard</button>
    </div>

    <script>
        let socket;
        let currentStudent = null;

        // Connect to backend when page loads
        window.onload = function() {
            socket = new WebSocket('ws://localhost:8003');
            
            socket.onopen = function() {
                document.getElementById('status').textContent = '✅ Connected to Backend (Port 8003)';
                document.getElementById('status').className = 'status connected';
            };

            socket.onerror = function(error) {
                document.getElementById('status').textContent = '❌ Connection Failed - Is backend running?';
                document.getElementById('status').className = 'status disconnected';
            };

            socket.onmessage = function(event) {
                handleMessage(JSON.parse(event.data));
            };
        };

        function handleMessage(response) {
            console.log('Received:', response);

            if (response.action === 'GET_STUDENT' && response.type === 'RESPONSE') {
                currentStudent = response.data;
                document.getElementById('studentName').textContent = currentStudent.name;
                document.getElementById('loginSection').style.display = 'none';
                document.getElementById('dashboardSection').style.display = 'block';
                loadModules();
            }
            else if (response.action === 'GET_STUDENT' && response.type === 'ERROR') {
                alert('Invalid Student ID!');
            }
            else if (response.action === 'GET_STUDENT_REGISTRATIONS' && response.type === 'RESPONSE') {
                showModules(response.data);
            }
            else if (response.action === 'GET_AVAILABLE_MODULES' && response.type === 'RESPONSE') {
                showAvailableModules(response.data);
            }
            else if (response.action === 'REGISTER_MODULE' && response.type === 'RESPONSE') {
                alert('Successfully registered!');
                loadAvailableModules();
                loadModules();
            }
            else if (response.action === 'REGISTER_MODULE' && response.type === 'ERROR') {
                alert('Registration failed: ' + response.error);
            }
        }

        function login() {
            const studentId = document.getElementById('studentId').value;
            socket.send(JSON.stringify({
                type: 'REQUEST',
                action: 'GET_STUDENT',
                data: { studentId: studentId }
            }));
        }

        function loadModules() {
            socket.send(JSON.stringify({
                type: 'REQUEST',
                action: 'GET_STUDENT_REGISTRATIONS',
                data: { studentId: currentStudent.id }
            }));
        }

        function showModules(registrations) {
            const table = document.getElementById('modulesTable');
            table.innerHTML = '';
            
            registrations.forEach(reg => {
                table.innerHTML += `
                    <tr>
                        <td>${reg.module.code}</td>
                        <td>${reg.module.name}</td>
                        <td>${reg.module.credits}</td>
                        <td>${reg.module.lecturer}</td>
                        <td>${reg.module.semester}</td>
                    </tr>
                `;
            });
        }

        function showRegistrationPage() {
            document.getElementById('dashboardSection').style.display = 'none';
            document.getElementById('registrationSection').style.display = 'block';
            loadAvailableModules();
        }

        function loadAvailableModules() {
            socket.send(JSON.stringify({
                type: 'REQUEST',
                action: 'GET_AVAILABLE_MODULES',
                data: { studentId: currentStudent.id }
            }));
        }

        function showAvailableModules(modules) {
            const container = document.getElementById('availableModules');
            container.innerHTML = '';
            
            modules.forEach(module => {
                container.innerHTML += `
                    <div style="border: 1px solid #ddd; padding: 15px; margin: 10px 0;">
                        <h3>${module.code}: ${module.name}</h3>
                        <p>${module.description}</p>
                        <p><strong>Lecturer:</strong> ${module.lecturer} | 
                           <strong>Credits:</strong> ${module.credits} | 
                           <strong>Semester:</strong> ${module.semester}</p>
                        <button onclick="registerForModule('${module.code}')">Register</button>
                    </div>
                `;
            });
        }

        function registerForModule(moduleCode) {
            socket.send(JSON.stringify({
                type: 'REQUEST',
                action: 'REGISTER_MODULE',
                data: {
                    studentId: currentStudent.id,
                    moduleCode: moduleCode
                }
            }));
        }

        function backToDashboard() {
            document.getElementById('registrationSection').style.display = 'none';
            document.getElementById('dashboardSection').style.display = 'block';
            loadModules();
        }
    </script>
</body>
</html>
```

---

## 🧪 Testing Your Portal

### Step 1: Start Backend

```bash
cd Backend
mvn exec:java "-Dexec.mainClass=Main"
```

Look for: `✅ WebSocket Server started on port 8003`

### Step 2: Open Your Portal

Open your HTML file in a browser. You should see "✅ Connected to Backend"

### Step 3: Test Login

Try these test student IDs:
- `std1`
- `std2`
- `std3`
- `std4`

### Step 4: Check Modules

After login, you should see the student's registered modules automatically.

### Step 5: Test Registration

Click "Register for New Modules" and try registering for available modules.

---

## 📋 Quick Reference: All Actions

| Action | Purpose | Data to Send |
|--------|---------|--------------|
| `GET_STUDENT` | Login/verify student | `{ studentId: "std1" }` |
| `GET_STUDENT_REGISTRATIONS` | Get student's modules | `{ studentId: "std1" }` |
| `GET_AVAILABLE_MODULES` | Get modules to register | `{ studentId: "std1" }` |
| `REGISTER_MODULE` | Register for module | `{ studentId: "std1", moduleCode: "CS102" }` |
| `GET_ALL_MODULES` | Get all modules | `{}` |

---

## ❌ Troubleshooting

| Problem | Solution |
|---------|----------|
| "Connection Failed" | Make sure backend is running: `mvn exec:java "-Dexec.mainClass=Main"` |
| "Student not found" | Try different student ID: std1, std2, std3, std4 |
| "Already registered" | Student is already in that module - try different one |
| No modules showing | Student hasn't registered yet - that's normal |
| WebSocket closes | Backend crashed - restart it |

---

## 🎯 Summary

### What Backend Provides on Port 8003:
✅ Student authentication (GET_STUDENT)  
✅ Student's registered modules (GET_STUDENT_REGISTRATIONS)  
✅ Available modules for registration (GET_AVAILABLE_MODULES)  
✅ Module registration (REGISTER_MODULE)

### What You Need to Do:
1. Connect to `ws://localhost:8003`
2. Send JSON messages in the format shown above
3. Handle JSON responses
4. Display data in your UI

### Networking Concepts Used:
🌐 **WebSocket Protocol** - Full-duplex communication over TCP  
🔌 **TCP Socket** - Reliable, connection-oriented communication  
📨 **Message-Based Communication** - JSON messages over persistent connection  
🔄 **Client-Server Architecture** - Your portal = client, Backend = server

**You're all set! Start building your student portal! 🚀**
