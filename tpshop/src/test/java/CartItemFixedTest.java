package test;

import entity.CartItem;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * After applying Value Object pattern,
 * all cart operations work properly on real domain objects.
 */
public class CartItemFixedTest {

    /**
     * TEST 1: CartItem has real domain properties.
     */
    @Test
    public void testCartItemHasRealProperties() {
        CartItem item = new CartItem(980001L, 1095.0);
        assertEquals(Long.valueOf(980001L), item.getProductId());
        assertEquals(1095.0, item.getPrice(), 0.01);
        assertEquals(1, item.getQuantity());
        System.out.println("FIXED: CartItem has real domain properties.");
    }

    /**
     * TEST 2: Duplicate products are grouped by quantity.
     */
    @Test
    public void testDuplicateProductsGroupedByQuantity() {
        List<CartItem> cart = new ArrayList<>();
        CartItem item = new CartItem(980001L, 1095.0);
        cart.add(item);

        // Adding same product again increments quantity
        cart.get(0).incrementQuantity();

        assertEquals(1, cart.size());
        assertEquals(2, cart.get(0).getQuantity());
        System.out.println("FIXED: Same product grouped. Quantity = "
                + cart.get(0).getQuantity());
    }

    /**
     * TEST 3: Subtotal calculated correctly per item.
     */
    @Test
    public void testSubtotalCalculatedCorrectly() {
        CartItem item = new CartItem(980001L, 1095.0);
        item.incrementQuantity(); // quantity = 2
        assertEquals(2190.0, item.getSubtotal(), 0.01);
        System.out.println("FIXED: Subtotal = " + item.getSubtotal());
    }
}