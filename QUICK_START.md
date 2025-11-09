# 🚀 Quick Start Guide

## Backend is Currently Running! ✅

Your backend servers are up and operational:
- ✅ **TCP Socket Server:** `localhost:8002`
- ✅ **WebSocket Server:** `ws://localhost:8003`

## Next Steps

### Start the Frontend

Open a **new terminal** and run:

```bash
cd "d:\Network Project\Frontend"
npm run dev
```

Then open your browser to: **http://localhost:5173**

You should see a **green "Connected to backend server" banner** at the top of the page!

## What's Working Now

### TCP Socket Server (Port 8002)
- Handles Admin and Student TCP clients
- Multithreaded client handling
- Each connection spawns a new thread
- Uses `ServerSocket`, `Socket`, `BufferedReader/Writer`

### WebSocket Server (Port 8003)
- Bridges web frontend to backend
- Converts JSON ↔ DataStore operations
- Real-time bidirectional communication

### DataStore (Thread-Safe)
- Uses `ConcurrentHashMap` for thread safety
- Handles Students, Modules, and Registrations
- File persistence using Java serialization

## Network Concepts Demonstrated

### ✅ TCP Socket Programming
- Server-Client architecture
- Socket connections
- Input/Output streams
- Request-Response protocol

### ✅ Multithreading
- Main thread
- Server thread (TCP)
- Client handler threads (one per connection)
- Thread-safe data structures

### ✅ Data Serialization
- Java Object Serialization (file I/O)
- JSON Serialization (WebSocket)

### ✅ Concurrent Programming
- `ConcurrentHashMap` for thread safety
- Synchronized methods for data integrity
- Thread lifecycle management

## Testing the System

### 1. Test via Web Frontend
- Add/Edit/Delete students
- Add/Edit/Delete modules
- View real-time updates

### 2. Test via TCP Client (Advanced)
You can connect directly to port 8002 using telnet or a Java TCP client:

```bash
telnet localhost 8002
```

Then send commands like:
```
ROLE|ADMIN
ADD_STUDENT|S001|John Doe|john@example.com|2024|1234567890|2024-01-15
LIST_STUDENTS
```

## Stopping the Servers

Press `Ctrl+C` in the terminal where Maven is running.

## Rebuilding After Changes

```bash
cd "d:\Network Project\Backend"
mvn clean install
mvn exec:java "-Dexec.mainClass=Main"
```

## Troubleshooting

### Frontend shows "Disconnected"?
- Check if backend is running (look for the startup messages)
- Verify WebSocket server on port 8003: `ws://localhost:8003`
- Check browser console for connection errors

### Port already in use?
```bash
# Find and kill process using port 8002 or 8003
netstat -ano | findstr :8002
taskkill /PID <PID> /F
```

### Build errors?
```bash
mvn clean
mvn install
```

## File Structure

```
Backend/
├── pom.xml                          # Maven configuration
├── src/
│   ├── Main.java                    # Application entry point
│   └── common/
│       ├── models/                  # Student, Module, ModuleRegistration
│       ├── network/                 # TCP & WebSocket servers
│       │   ├── NetworkServer.java   # TCP Socket Server
│       │   ├── UniversityWebSocketServer.java  # WebSocket Bridge
│       │   ├── ClientHandler.java   # Abstract handler
│       │   ├── AdminClientHandler.java
│       │   ├── StudentClientHandler.java
│       │   ├── LocalDateAdapter.java
│       │   └── LocalDateTimeAdapter.java
│       ├── storage/                 # Data management
│       │   ├── DataStore.java       # Thread-safe CRUD
│       │   └── PersistenceManager.java  # File I/O
│       └── utils/
│           └── ValidationUtils.java # Validation logic
```

## Enjoy Your Network Programming Project! 🎓

Your backend now demonstrates:
- ✅ TCP Socket Programming
- ✅ Multithreading
- ✅ Concurrent Data Access
- ✅ Client-Server Architecture
- ✅ Data Serialization
- ✅ File I/O
- ✅ Network Protocol Design
