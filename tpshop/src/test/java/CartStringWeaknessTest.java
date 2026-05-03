package test;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Demonstrates the weakness of storing cart items as Strings.
 * Proves that String-based cart cannot support real business operations.
 */
public class CartStringWeaknessTest {

    /**
     * TEST 1: Proves that duplicate products cannot be grouped.
     * Adding the same product twice creates two separate entries.
     * There is no way to know quantity = 2.
     */
    @Test
    public void testDuplicateProductsCannotBeGrouped() {
        List<String> cart = new ArrayList<>();

        // Simulating original addProduct logic
        cart.add("ID=980001" + " = " + 1095.0 + "$");
        cart.add("ID=980001" + " = " + 1095.0 + "$");

        // Two separate entries — no grouping possible
        assertEquals(2, cart.size());

        // We CANNOT get quantity of product 980001
        // We would have to parse the String manually
        // That is not domain logic, that is text manipulation
        System.out.println("PROOF: Same product appears " + cart.size() + " times.");
        System.out.println("There is no way to call item.getQuantity().");
        System.out.println("String-based cart has no domain meaning.");
    }

    /**
     * TEST 2: Proves that you cannot remove a specific product.
     * To remove product 980001, you must parse every String.
     */
    @Test
    public void testCannotRemoveSpecificProduct() {
        List<String> cart = new ArrayList<>();
        cart.add("ID=980001 = 1095.0$");
        cart.add("ID=980005 = 11500.99$");

        // To remove product 980001 we have to do string manipulation
        // There is no cart.remove(productId) possible
        String toRemove = null;
        for (String item : cart) {
            if (item.contains("980001")) {
                toRemove = item;
                break;
            }
        }
        cart.remove(toRemove);

        assertEquals(1, cart.size());
        System.out.println("PROOF: Removing a product requires string parsing.");
        System.out.println("No proper domain method exists.");
        System.out.println("cart.remove(productId) is impossible with List<String>.");
    }

    /**
     * TEST 3: Proves that you cannot apply discount per item.
     * To apply 10% discount, you must parse and rebuild every String.
     */
    @Test
    public void testCannotApplyDiscountPerItem() {
        List<String> cart = new ArrayList<>();
        cart.add("ID=980001 = 1095.0$");

        // To apply discount we must parse the String
        // Extract price, calculate discount, rebuild String
        // This is NOT business logic, this is text hacking
        String item = cart.get(0);
        String[] parts = item.split(" = ");
        double price = Double.parseDouble(parts[1].replace("$", ""));
        double discounted = price * 0.9;

        // We had to hack a String just to apply business logic
        assertEquals(985.5, discounted, 0.01);
        System.out.println("PROOF: Applying discount required String parsing.");
        System.out.println("item.applyDiscount(0.10) is impossible with List<String>.");
        System.out.println("Business logic cannot live in text manipulation.");
    }
}