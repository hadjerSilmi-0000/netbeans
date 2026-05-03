
package EJB.plugin; 
public class LoyaltyPlugin implements CartPlugin { 
    @Override 
    public String execute(Long productId, int quantity) { 
        return "You earned " + (quantity * 50) + " loyalty points!"; 
    } 
} 