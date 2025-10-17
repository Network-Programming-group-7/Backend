package common.network;

import java.io.Serializable;
import java.util.Date;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;
    private Object data;
    private String sessionId;
    private Date timestamp;
    
    public Request() {
        this.timestamp = new Date();
    }
    
    public Request(String type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = new Date();
    }
    
    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}