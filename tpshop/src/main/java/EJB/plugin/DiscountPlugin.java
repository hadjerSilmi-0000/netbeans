
package EJB.plugin; 
public class DiscountPlugin implements CartPlugin { 
    @Override 
public String execute(Long productId, int quantity) { 
    if (quantity >= 2) return "You saved 10%! Bulk discount applied!"; 
    return "Buy 2+ items for a 10% discount!"; 
    } 
}