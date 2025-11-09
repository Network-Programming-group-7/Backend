package member1.service;

import common.network.*;

public class AuthenticationService {

    public Response handleAuth(Request req) {
        LoginRequest login = (LoginRequest) req.getData();

        if ("admin".equals(login.getUsername()) &&
                "admin123".equals(login.getPassword()) &&
                "Admin".equalsIgnoreCase(login.getRole())) {

            return Response.success("Login successful");
        } else {
            return Response.failure("Invalid username, password, or role");
        }
    }
}
