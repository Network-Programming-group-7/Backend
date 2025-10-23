package member5.service;

import common.models.Module;
import common.models.Result;
import common.models.ModuleRegistration;
import common.network.Request;
import common.network.Response;
import common.utils.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch results from Member 4's ResultManagementServer
 * and modules from Member 2's StudentModuleServer
 */
public class ResultViewService {
    
    /**
     * Get all results for a student
     * Connects to Member 4's server (Port 8004)
     */
    public List<Result> getStudentResults(String studentId) {
        System.out.println("🔗 Connecting to Result Management Server...");
        
        // For now, return mock data
        // TODO: Connect to Member 4's server when ready
        return getMockResults(studentId);
        
        /* UNCOMMENT WHEN MEMBER 4 IS READY:
        try {
            Socket socket = new Socket(Constants.SERVER_HOST, Constants.RESULT_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            // Create request
            Request request = new Request(Constants.GET_STUDENT_RESULTS, studentId);
            out.writeObject(request);
            out.flush();
            
            // Read response
            Response response = (Response) in.readObject();
            
            socket.close();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                return (List<Result>) response.getData();
            } else {
                System.err.println("Error fetching results: " + response.getMessage());
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            System.err.println("Error connecting to Result Management Server");
            e.printStackTrace();
            return getMockResults(studentId); // Fallback to mock data
        }
        */
    }
    
    /**
     * Get result for specific module
     */
    public Result getModuleResult(String studentId, String moduleCode) {
        List<Result> allResults = getStudentResults(studentId);
        
        for (Result result : allResults) {
            if (result.getModuleCode().equals(moduleCode)) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Register for a module
     * Connects to Member 2's server (Port 8002)
     */
    public boolean registerForModule(String studentId, String moduleCode) {
        System.out.println("🔗 Connecting to Student Module Server...");
        
        // For now, return true (mock)
        // TODO: Connect to Member 2's server when ready
        return true;
        
        /* UNCOMMENT WHEN MEMBER 2 IS READY:
        try {
            Socket socket = new Socket(Constants.SERVER_HOST, Constants.STUDENT_MODULE_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            // Create registration
            ModuleRegistration registration = new ModuleRegistration(studentId, moduleCode);
            Request request = new Request(Constants.REGISTER_MODULE, registration);
            out.writeObject(request);
            out.flush();
            
            // Read response
            Response response = (Response) in.readObject();
            
            socket.close();
            
            return response.getStatus().equals(Constants.SUCCESS);
            
        } catch (Exception e) {
            System.err.println("Error connecting to Student Module Server");
            e.printStackTrace();
            return false;
        }
        */
    }
    
    /**
     * Get registered modules for student
     */
    public List<Module> getRegisteredModules(String studentId) {
        System.out.println("🔗 Fetching registered modules...");
        
        // For now, return mock data
        // TODO: Connect to Member 2's server when ready
        return getMockModules();
        
        /* UNCOMMENT WHEN MEMBER 2 IS READY:
        try {
            Socket socket = new Socket(Constants.SERVER_HOST, Constants.STUDENT_MODULE_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            Request request = new Request(Constants.GET_STUDENT_MODULES, studentId);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            socket.close();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                return (List<Module>) response.getData();
            } else {
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching registered modules");
            e.printStackTrace();
            return getMockModules();
        }
        */
    }
    
    /**
     * Get available modules for registration
     */
    public List<Module> getAvailableModules(String studentId) {
        System.out.println("🔗 Fetching available modules...");
        
        // For now, return mock data
        // TODO: Connect to Member 2's server when ready
        return getMockModules();
        
        /* UNCOMMENT WHEN MEMBER 2 IS READY:
        try {
            Socket socket = new Socket(Constants.SERVER_HOST, Constants.STUDENT_MODULE_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            Request request = new Request(Constants.GET_AVAILABLE_MODULES, studentId);
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            
            socket.close();
            
            if (response.getStatus().equals(Constants.SUCCESS)) {
                return (List<Module>) response.getData();
            } else {
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching available modules");
            e.printStackTrace();
            return getMockModules();
        }
        */
    }
    
    // ==================== MOCK DATA METHODS ====================
    // These are for testing without other servers running
    // Remove when integrating with real servers
    
    private List<Result> getMockResults(String studentId) {
        List<Result> results = new ArrayList<>();
        
        Result r1 = new Result(studentId, "CS3111", 85);
        r1.setId("R001");
        r1.setModuleName("Network Programming");
        r1.setGrade("A");
        r1.setCredits(3);
        results.add(r1);
        
        Result r2 = new Result(studentId, "CS3112", 78);
        r2.setId("R002");
        r2.setModuleName("Database Systems");
        r2.setGrade("B+");
        r2.setCredits(3);
        results.add(r2);
        
        Result r3 = new Result(studentId, "CS3113", 92);
        r3.setId("R003");
        r3.setModuleName("Software Engineering");
        r3.setGrade("A+");
        r3.setCredits(4);
        results.add(r3);
        
        Result r4 = new Result(studentId, "CS3114", 65);
        r4.setId("R004");
        r4.setModuleName("Web Development");
        r4.setGrade("B");
        r4.setCredits(3);
        results.add(r4);
        
        System.out.println("📊 Returning " + results.size() + " mock results");
        return results;
    }
    
    private List<Module> getMockModules() {
        List<Module> modules = new ArrayList<>();
        
        Module m1 = new Module("CS3115", "Mobile Development", 3);
        m1.setSemester("2");
        modules.add(m1);
        
        Module m2 = new Module("CS3116", "Cloud Computing", 3);
        m2.setSemester("2");
        modules.add(m2);
        
        Module m3 = new Module("CS3117", "Machine Learning", 4);
        m3.setSemester("2");
        modules.add(m3);
        
        System.out.println("📚 Returning " + modules.size() + " mock modules");
        return modules;
    }
}