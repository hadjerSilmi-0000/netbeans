package EJB; 
import entity.CartItem; 
import java.util.List; 


public interface ShopService { 
    String addProductToCart(String productParam, MiniShopFull cartEJB); 
    double getLastPrice(); 
    List<CartItem> getCartItems(MiniShopFull cartEJB); 
    double getCartTotal(MiniShopFull cartEJB); 
}