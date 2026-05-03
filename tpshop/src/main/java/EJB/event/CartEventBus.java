package EJB.event; 
import jakarta.ejb.Singleton; 
import java.util.ArrayList; 
import java.util.List; 

@Singleton 
public class CartEventBus { 
    private List<CartEventListener> listeners = new ArrayList<>(); 
    
    public void subscribe(CartEventListener listener) { 
        listeners.add(listener); 
    } 
    
    public void publish(CartEvent event) { 
        System.out.println("EventBus: Publishing -> " + event.getEventType()); 
        for (CartEventListener listener : listeners) { 
            try { listener.onCartEvent(event); } 
            catch (Exception e) { 
                System.err.println("Listener failed: " + e.getMessage()); 
            } 
        } 
    } 
} 