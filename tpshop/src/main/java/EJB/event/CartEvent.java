package EJB.event; 
import java.io.Serializable; 
import java.time.LocalDateTime; 

public class CartEvent implements Serializable { 
    private Long productId; 
    private int quantity; 
    private String eventType; 
    private LocalDateTime timestamp; 
    
    public CartEvent(Long productId, int quantity, String eventType) { 
        this.productId = productId; 
        this.quantity = quantity; 
        this.eventType = eventType; 
        this.timestamp = LocalDateTime.now(); 
    } 
    public Long getProductId() { return productId; } 
    public int getQuantity() { return quantity; } 
    public String getEventType() { return eventType; } 
    public LocalDateTime getTimestamp() { return timestamp; } 
}