package EJB.plugin; 
public class SuggestionPlugin implements CartPlugin { 
    @Override 
    public String execute(Long productId, int quantity) { 
        return "You might also like: Product #" + (productId + 1); 
    } 
} 