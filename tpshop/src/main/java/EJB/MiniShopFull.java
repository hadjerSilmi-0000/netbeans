package EJB;

import EJB.event.CartEvent;
import EJB.event.CartEventBus;
import EJB.plugin.CartPlugin;
import EJB.plugin.PluginRegistry;
import entity.CartItem;
import jakarta.ejb.Stateful;
import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.EJB;

/**
 * Stateful EJB acting as shopping cart.
 * IMPROVED: stores CartItem objects instead of Strings.
 * 
 * Pattern: Domain Model / Value Object
 */
@Stateful
public class MiniShopFull {

    @EJB
    private PluginRegistry pluginRegistry;  // inject registry
    @EJB
    private CartEventBus eventBus;  // inject the event bus
    private double total = 0;
    private List<CartItem> items = new ArrayList<>();
    private List<String> lastPluginMessages = new ArrayList<>(); 
  
    /**
     * Adds a product to the cart.
     * If product already exists, increments quantity instead
     * of creating a duplicate entry.
     */
    public void addProduct(Long productId, double price) {

        // Check if product already in cart
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                item.incrementQuantity();
                total += price;
                runPlugins(productId, item.getQuantity());
                eventBus.publish(new CartEvent( 
                    productId, item.getQuantity(), "PRODUCT_ADDED")); 
                return;
            }
        }

        // Product not in cart yet — add new CartItem
        items.add(new CartItem(productId, price));
        total += price;
        runPlugins(productId, 1); 
        eventBus.publish(new CartEvent(productId, 1, "PRODUCT_ADDED")); 
        
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public void reset() {
        total = 0;
        items.clear();
    }

    private void runPlugins(Long productId, int quantity) { 
        lastPluginMessages = new ArrayList<>(); 
        for (CartPlugin plugin : pluginRegistry.getPlugins()) { 
            lastPluginMessages.add(plugin.execute(productId, quantity)); 
        } 
    } 
    
    public List<String> getLastPluginMessages() { 
        return lastPluginMessages; 
    } 
}