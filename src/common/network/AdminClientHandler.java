package common.network;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import common.models.Module;
import common.models.Student;
import common.storage.DataStore;

public class AdminClientHandler extends ClientHandler {
    public AdminClientHandler(java.net.Socket socket, DataStore store) {
        super(socket, store);
    }

    @Override
    protected String handle(String cmdLine) {
        if (cmdLine.isEmpty()) return "ERROR|Empty";
        String[] p = cmdLine.split("\\|");
        String cmd = p[0].toUpperCase();

        try {
            switch (cmd) {
                case "ADD_STUDENT": {
                    if (p.length < 7) return "ERROR|Usage ADD_STUDENT|id|name|email|batch|phone|yyyy-MM-dd";
                    Student s = new Student(p[1], p[2], p[3], p[4], p[5], LocalDate.parse(p[6]));
                    return store.addStudent(s) ? "OK|StudentAdded" : "ERROR|AddFailed";
                }
                case "VIEW_STUDENT": {
                    if (p.length < 2) return "ERROR|Usage VIEW_STUDENT|id";
                    Student s = store.getStudent(p[1]);
                    return s == null ? "ERROR|NotFound" : "OK|" + s.toString();
                }
                case "LIST_STUDENTS": {
                    List<Student> list = store.listStudents();
                    return "OK|" + list.stream().map(Student::toString).collect(Collectors.joining(";;"));
                }
                case "UPDATE_STUDENT": {
                    if (p.length < 4) return "ERROR|Usage UPDATE_STUDENT|id|field|value";
                    return store.updateStudentField(p[1], p[2], p[3]) ? "OK|StudentUpdated" : "ERROR|UpdateFailed";
                }
                case "DELETE_STUDENT": {
                    if (p.length < 2) return "ERROR|Usage DELETE_STUDENT|id";
                    return store.deleteStudent(p[1]) ? "OK|StudentDeleted" : "ERROR|DeleteFailed";
                }
                case "ADD_MODULE": {
                    if (p.length < 7) return "ERROR|Usage ADD_MODULE|code|name|credits|lecturer|semester|description";
                    Module m = new Module(p[1], p[2], Integer.parseInt(p[3]), p[4], Integer.parseInt(p[5]), p[6]);
                    return store.addModule(m) ? "OK|ModuleAdded" : "ERROR|AddFailed";
                }
                case "VIEW_MODULE": {
                    if (p.length < 2) return "ERROR|Usage VIEW_MODULE|code";
                    Module m = store.getModule(p[1]);
                    return m == null ? "ERROR|NotFound" : "OK|" + m.toString();
                }
                case "LIST_MODULES": {
                    List<Module> list = store.listModules();
                    return "OK|" + list.stream().map(Module::toString).collect(Collectors.joining(";;"));
                }
                case "UPDATE_MODULE": {
                    if (p.length < 4) return "ERROR|Usage UPDATE_MODULE|code|field|value";
                    return store.updateModuleField(p[1], p[2], p[3]) ? "OK|ModuleUpdated" : "ERROR|UpdateFailed";
                }
                case "DELETE_MODULE": {
                    if (p.length < 2) return "ERROR|Usage DELETE_MODULE|code";
                    return store.deleteModule(p[1]) ? "OK|ModuleDeleted" : "ERROR|DeleteFailed";
                }
                default:
                    return "ERROR|UnknownCommand";
            }
        } catch (Exception e) {
            return "ERROR|" + e.getClass().getSimpleName() + ":" + e.getMessage();
        }
    }
}