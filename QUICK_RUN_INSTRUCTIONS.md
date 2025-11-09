# 🚀 Quick Run Instructions

## How to Start the University Management System

Follow these steps **in order** to run the complete system:

---

## Step 1: Start the Backend Server ⚙️

### Terminal 1 - Backend

```bash
# Navigate to Backend folder
cd "d:\Network Project\Backend"

# Start the backend servers (TCP + WebSocket)
mvn exec:java "-Dexec.mainClass=Main"
```

### Wait for Success Messages:
You should see:
```
🚀 Starting TCP Socket Server...
✅ TCP Server running on port 8002
🚀 Starting WebSocket Server...
✅ WebSocket Server running on port 8003

==================================================
🎓 UNIVERSITY MANAGEMENT SYSTEM - SERVER READY
==================================================
📡 TCP Socket:    localhost:8002
🌐 WebSocket:     ws://localhost:8003
==================================================

🚀 WebSocket Server started successfully!
📡 Listening on: ws://localhost:8003
```

✅ **Backend is now running!** Keep this terminal open.

---

## Step 2: Start the Frontend Application 🎨

### Terminal 2 - Frontend (New Terminal)

```bash
# Navigate to Frontend folder
cd "d:\Network Project\Frontend"

# Install dependencies (first time only)
npm install

# Start the development server
npm run dev
```

### Wait for Success Message:
You should see:
```
  VITE v7.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

✅ **Frontend is now running!**

---

## Step 3: Open the Application 🌐

Open your browser and go to:
```
http://localhost:5173
```

You should see:
- ✅ Admin Dashboard
- ✅ Navigation tabs (Students, Lecturers, Batches, Modules, Exams)
- ✅ Green "Connected to backend server" banner (if backend is running)
- ✅ Student and Module management tables

---

## 🎯 Features You Can Use

### Student Management
- ✅ **Add Student** - Click "+ Add Student" button → Fill form → Click "Add Student"
- ✅ **Edit Student** - Click edit icon (✏️) on any student → Modify → Click "Update Student"
- ✅ **Delete Student** - Click delete icon (🗑️) on any student → Confirm deletion
- ✅ **Search** - Type in search box to filter students

### Module Management
- ✅ **Add Module** - Click "+ Add Module" button → Fill form → Click "Add Module"
- ✅ **Edit Module** - Click edit icon (✏️) on any module → Modify → Click "Update Module"
- ✅ **Delete Module** - Click delete icon (🗑️) on any module → Confirm deletion
- ✅ **Search** - Type in search box to filter modules

---

## ⚠️ Important Notes

### Backend MUST Run First!
The **backend must be running** before you start the frontend if you want:
- ✅ Data persistence (saved to files)
- ✅ WebSocket connection
- ✅ Real-time updates
- ✅ TCP socket communication

### Without Backend (Frontend Only):
If you only run the frontend:
- ✅ Modals still work
- ✅ Add/Edit/Delete functionality works
- ⚠️ Data only exists in browser memory (lost on refresh)
- ⚠️ Red "Disconnected from backend" banner appears

---

## 🛑 How to Stop the Servers

### Stop Backend:
In the backend terminal, press:
```
Ctrl + C
```

### Stop Frontend:
In the frontend terminal, press:
```
Ctrl + C
```

---

## 🔄 Restart Instructions

If you need to restart:

1. **Stop both servers** (Ctrl + C in each terminal)
2. **Start backend first** (Step 1)
3. **Wait for backend ready message**
4. **Then start frontend** (Step 2)

---

## 🐛 Troubleshooting

### Port Already in Use?

**Backend Port 8002 or 8003 busy:**
```bash
# Find process using the port
netstat -ano | findstr :8002
netstat -ano | findstr :8003

# Kill the process (replace <PID> with actual number)
taskkill /PID <PID> /F
```

**Frontend Port 5173 busy:**
```bash
# Find process
netstat -ano | findstr :5173

# Kill process
taskkill /PID <PID> /F
```

### Frontend Shows "Disconnected"?

1. ✅ Check backend is running (look for success messages)
2. ✅ Verify WebSocket on port 8003: `ws://localhost:8003`
3. ✅ Check browser console for errors (F12 → Console)
4. ✅ Try restarting backend first, then frontend

### Build Errors in Backend?

```bash
# Clean and rebuild
cd "d:\Network Project\Backend"
mvn clean install
mvn exec:java "-Dexec.mainClass=Main"
```

### Dependencies Missing in Frontend?

```bash
cd "d:\Network Project\Frontend"
npm install
npm run dev
```

---

## 📊 System Architecture

```
Browser (http://localhost:5173)
        ↓ WebSocket
WebSocket Server (Port 8003)
        ↓ Uses
DataStore (Thread-safe CRUD operations)
        ↑ Uses
TCP Socket Server (Port 8002)
        ↓ TCP
Admin/Student Clients
```

---

## 📝 Quick Reference

| Component | Port | Protocol | Purpose |
|-----------|------|----------|---------|
| Frontend | 5173 | HTTP | Web UI |
| WebSocket Server | 8003 | WebSocket | Browser ↔ Backend bridge |
| TCP Server | 8002 | TCP Socket | Client connections |

---

## ✨ Summary

**Simple 3-Step Process:**

1. **Backend First** → `mvn exec:java "-Dexec.mainClass=Main"`
2. **Wait for Ready** → See success messages
3. **Frontend Second** → `npm run dev`

**Then open:** http://localhost:5173

**That's it! Your system is now running! 🎉**

---

## 📚 Need More Help?

Check these files:
- `BACKEND_FIX_SUMMARY.md` - Backend architecture details
- `MODAL_UPDATE_SUMMARY.md` - Frontend modal features
- `DETAILED_CHANGES.md` - Code changes explained
- `COMPLETE_UPDATE_SUMMARY.md` - Full project overview
