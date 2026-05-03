/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EJB.event;

import jakarta.ejb.EJB; 
import jakarta.ejb.Singleton; 
import jakarta.annotation.PostConstruct; 
import jakarta.ejb.Startup;

@Singleton 
@Startup
public class LogListener implements CartEventListener { 
    @EJB private CartEventBus eventBus; 
    
    @PostConstruct 
    public void register() { eventBus.subscribe(this); } 
    
    @Override 
    public void onCartEvent(CartEvent event) { 
        System.out.println("LogListener: [" + event.getTimestamp() 
            + "] " + event.getEventType() 
            + " -> Product " + event.getProductId()); 
    } 
} 