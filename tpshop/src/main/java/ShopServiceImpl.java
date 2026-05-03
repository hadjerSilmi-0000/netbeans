package EJB; 
import entity.CartItem; 
import jakarta.ejb.EJB; 
import jakarta.ejb.Stateless; 
import java.util.List; 

@Stateless 
public class ShopServiceImpl implements ShopService { 
    
    @EJB 
    private MiniShopEJB miniShopEJB;  // ShopService talks to EJB #1 
    
    @Override 
    public String addProductToCart(String productParam, MiniShopFull cartEJB) { 
        return miniShopEJB.processAddToCart(productParam, cartEJB); 
    } 
    
    @Override 
    public double getLastPrice() { 
        return miniShopEJB.getLastPrice(); 
    } 
    
    @Override 
    public List<CartItem> getCartItems(MiniShopFull cartEJB) { 
        return cartEJB.getItems(); 
    } 
    
    @Override 
    public double getCartTotal(MiniShopFull cartEJB) { 
        return cartEJB.getTotal(); 
    } 
}