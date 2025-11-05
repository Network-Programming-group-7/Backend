package network;

import common.models.Module;
import common.models.ModuleRegistration;
import storage.DataStore;

import java.util.List;
import java.util.stream.Collectors;

public class StudentClientHandler extends ClientHandler {
    public StudentClientHandler(java.net.Socket socket, DataStore store) {
        super(socket, store);
    }

    @Override
    protected String handle(String cmdLine) {
        if (cmdLine.isEmpty()) return "ERROR|Empty";
        String[] p = cmdLine.split("\\|");
        String cmd = p[0].toUpperCase();

        try {
            switch (cmd) {
                case "REGISTER": {
                    if (p.length < 4) return "ERROR|Usage REGISTER|studentId|moduleCode|semester";
                    String res = store.registerModule(p[1], p[2], Integer.parseInt(p[3]));
                    return res;
                }
                case "LIST_AVAILABLE": {
                    if (p.length < 3) return "ERROR|Usage LIST_AVAILABLE|studentId|semester";
                    List<Module> list = store.listAvailableModulesForStudent(p[1], Integer.parseInt(p[2]));
                    return "OK|" + list.stream().map(Module::toString).collect(Collectors.joining(";;"));
                }
                case "LIST_REGISTERED": {
                    if (p.length < 3) return "ERROR|Usage LIST_REGISTERED|studentId|semester";
                    List<ModuleRegistration> list = store.listRegistrationsForStudent(p[1], Integer.parseInt(p[2]));
                    return "OK|" + list.stream().map(ModuleRegistration::toString).collect(Collectors.joining(";;"));
                }
                default:
                    return "ERROR|UnknownCommand";
            }
        } catch (Exception e) {
            return "ERROR|" + e.getClass().getSimpleName() + ":" + e.getMessage();
        }
    }
}