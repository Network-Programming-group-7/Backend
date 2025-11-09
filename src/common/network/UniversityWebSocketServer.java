package common.network;

import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.models.Module;
import common.models.ModuleRegistration;
import common.models.Student;
import common.storage.DataStore;

/**
 * WebSocket Server - Bridges Web Frontend to TCP Backend
 * Converts WebSocket messages to DataStore operations
 */
public class UniversityWebSocketServer extends WebSocketServer {
    private final DataStore dataStore;
    private final Gson gson;

    public UniversityWebSocketServer(int port, DataStore dataStore) {
        super(new InetSocketAddress(port));
        this.dataStore = dataStore;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("✅ WebSocket client connected: " + conn.getRemoteSocketAddress());
        sendResponse(conn, "CONNECTED", Map.of("message", "Welcome to University Management System"));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("❌ WebSocket client disconnected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            System.out.println("📨 Received: " + message);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> request = gson.fromJson(message, Map.class);
            
            String type = (String) request.get("type");
            String action = (String) request.get("action");
            
            if (!"REQUEST".equals(type)) {
                sendError(conn, "Invalid message type");
                return;
            }
            
            Object data = request.get("data");
            Object result = handleRequest(action, data);
            sendResponse(conn, action, result);
            
        } catch (Exception e) {
            System.err.println("❌ Error processing message: " + e.getMessage());
            e.printStackTrace();
            sendError(conn, e.getMessage());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("❌ WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("🚀 WebSocket Server started successfully!");
        System.out.println("📡 Listening on: ws://localhost:" + getPort());
    }

    @SuppressWarnings("unchecked")
    private Object handleRequest(String action, Object data) throws Exception {
        Map<String, Object> dataMap = data != null ? (Map<String, Object>) data : Map.of();
        
        return switch (action) {
            // ===== STUDENT OPERATIONS =====
            case "GET_ALL_STUDENTS" -> {
                List<Student> students = dataStore.listStudents();
                System.out.println("📚 Fetched " + students.size() + " students");
                yield students;
            }
            
            case "GET_STUDENT" -> {
                String id = (String) dataMap.get("id");
                Student student = dataStore.getStudent(id);
                if (student == null) {
                    throw new Exception("Student not found: " + id);
                }
                yield student;
            }
            
            case "ADD_STUDENT" -> {
                Student student = gson.fromJson(gson.toJson(data), Student.class);
                boolean success = dataStore.addStudent(student);
                if (!success) {
                    throw new Exception("Failed to add student - ID may already exist or validation failed");
                }
                System.out.println("✅ Added student: " + student.getId());
                yield student;
            }
            
            case "UPDATE_STUDENT" -> {
                Student student = gson.fromJson(gson.toJson(data), Student.class);
                // For updates, we need to validate the student exists first
                if (dataStore.getStudent(student.getId()) == null) {
                    throw new Exception("Student not found: " + student.getId());
                }
                // Delete and re-add (since we don't have a direct update method for full objects)
                dataStore.deleteStudent(student.getId());
                dataStore.addStudent(student);
                System.out.println("✅ Updated student: " + student.getId());
                yield student;
            }
            
            case "DELETE_STUDENT" -> {
                String id = (String) dataMap.get("id");
                boolean success = dataStore.deleteStudent(id);
                if (!success) {
                    throw new Exception("Student not found: " + id);
                }
                System.out.println("✅ Deleted student: " + id);
                yield Map.of("success", true, "id", id);
            }
            
            // ===== MODULE OPERATIONS =====
            case "GET_ALL_MODULES" -> {
                List<Module> modules = dataStore.listModules();
                System.out.println("📚 Fetched " + modules.size() + " modules");
                yield modules;
            }
            
            case "GET_MODULE" -> {
                String code = (String) dataMap.get("code");
                Module module = dataStore.getModule(code);
                if (module == null) {
                    throw new Exception("Module not found: " + code);
                }
                yield module;
            }
            
            case "ADD_MODULE" -> {
                Module module = gson.fromJson(gson.toJson(data), Module.class);
                boolean success = dataStore.addModule(module);
                if (!success) {
                    throw new Exception("Failed to add module - Code may already exist or validation failed");
                }
                System.out.println("✅ Added module: " + module.getCode());
                yield module;
            }
            
            case "UPDATE_MODULE" -> {
                Module module = gson.fromJson(gson.toJson(data), Module.class);
                // Validate module exists
                if (dataStore.getModule(module.getCode()) == null) {
                    throw new Exception("Module not found: " + module.getCode());
                }
                // Delete and re-add
                dataStore.deleteModule(module.getCode());
                dataStore.addModule(module);
                System.out.println("✅ Updated module: " + module.getCode());
                yield module;
            }
            
            case "DELETE_MODULE" -> {
                String code = (String) dataMap.get("code");
                boolean success = dataStore.deleteModule(code);
                if (!success) {
                    throw new Exception("Module not found: " + code);
                }
                System.out.println("✅ Deleted module: " + code);
                yield Map.of("success", true, "code", code);
            }
            
            // ===== MODULE REGISTRATION OPERATIONS =====
            case "REGISTER_MODULE" -> {
                ModuleRegistration registration = gson.fromJson(gson.toJson(data), ModuleRegistration.class);
                String result = dataStore.registerModule(
                    registration.getStudentId(),
                    registration.getModuleCode(),
                    registration.getSemester()
                );
                if (result.startsWith("ERROR")) {
                    throw new Exception(result.substring(6)); // Remove "ERROR|" prefix
                }
                System.out.println("✅ Registered module: " + registration.getModuleCode() + 
                                 " for student: " + registration.getStudentId());
                yield registration;
            }
            
            case "GET_STUDENT_REGISTRATIONS" -> {
                String studentId = (String) dataMap.get("studentId");
                Double semesterDouble = (Double) dataMap.get("semester");
                int semester = semesterDouble != null ? semesterDouble.intValue() : 1;
                List<ModuleRegistration> registrations = dataStore.listRegistrationsForStudent(studentId, semester);
                System.out.println("📚 Fetched " + registrations.size() + " registrations for student: " + studentId);
                yield registrations;
            }
            
            case "GET_AVAILABLE_MODULES" -> {
                String studentId = (String) dataMap.get("studentId");
                Double semesterDouble = (Double) dataMap.get("semester");
                int semester = semesterDouble != null ? semesterDouble.intValue() : 1;
                List<Module> modules = dataStore.listAvailableModulesForStudent(studentId, semester);
                System.out.println("📚 Fetched " + modules.size() + " available modules for student: " + studentId);
                yield modules;
            }
            
            default -> throw new Exception("Unknown action: " + action);
        };
    }

    private void sendResponse(WebSocket conn, String action, Object data) {
        Map<String, Object> response = Map.of(
            "type", "RESPONSE",
            "action", action,
            "data", data
        );
        String json = gson.toJson(response);
        conn.send(json);
        System.out.println("📤 Sent response for: " + action);
    }

    private void sendError(WebSocket conn, String errorMessage) {
        Map<String, Object> response = Map.of(
            "type", "ERROR",
            "error", errorMessage
        );
        String json = gson.toJson(response);
        conn.send(json);
        System.out.println("📤 Sent error: " + errorMessage);
    }
}
