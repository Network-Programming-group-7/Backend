package common.network;

import java.io.Serializable;
import java.util.Date;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status;   // SUCCESS / ERROR / FAILED
    private String message;  // descriptive message
    private Object data;     // payload data
    private Date timestamp;

    public Response() {
        this.timestamp = new Date();
    }

    public Response(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = new Date();
    }

    // ✅ Success Responses
    public static Response success(String message) {
        return new Response("SUCCESS", message, null);
    }

    public static Response success(Object data) {
        return new Response("SUCCESS", "Operation successful", data);
    }

    // ✅ Failure Responses
    public static Response failure(String message) {
        return new Response("FAILED", message, null);
    }

    // ✅ Error Responses
    public static Response error(String message) {
        return new Response("ERROR", message, null);
    }

    // --- Getters and Setters ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
