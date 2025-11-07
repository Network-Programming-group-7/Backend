package common.network;

import java.io.Serializable;
import java.util.Date;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String status;
    private String message;
    private Object data;
    private Date timestamp;
    
    public Response() {
        this.timestamp = new Date();
    }
    
    public Response(String status, Object data) {
        this.status = status;
        this.data = data;
        this.timestamp = new Date();
    }
    
    public Response(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = new Date();
    }
    

    public static Response success(Object data) {
        return new Response("SUCCESS", data);
    }
    
    public static Response error(String message) {
        return new Response("ERROR", message, null);
    }
    
    public static Response failure(String message) {
        return new Response("FAILED", message, null);
    }
    
    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}