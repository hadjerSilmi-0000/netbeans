package EJB.event; 
import jakarta.ejb.EJB; 
import jakarta.ejb.Singleton; 
import jakarta.annotation.PostConstruct; 
import jakarta.ejb.Startup;

@Singleton 
@Startup
public class NotificationListener implements CartEventListener { 
    @EJB private CartEventBus eventBus; 
    
    @PostConstruct 
    public void register() { eventBus.subscribe(this); } 
    
    @Override 
    public void onCartEvent(CartEvent event) { 
        if ("PRODUCT_ADDED".equals(event.getEventType())) { 
            System.out.println("NotificationListener: User notified -" 
                + " Product " + event.getProductId() 
                + " added to cart!"); 
        } 
    } 
} 