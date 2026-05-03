package test;

import EJB.MiniShopEJB;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * After applying the MVC fix, business logic is now
 * in MiniShopEJB and CAN be tested directly
 * without any HTTP environment.
 */
public class SRPFixedTest {

    /**
     * TEST: Validation can now be tested directly
     * by calling the EJB method — no HTTP needed.
     */
    @Test
    public void testNullProductReturnsError() {
        MiniShopEJB ejb = new MiniShopEJB();
        String result = ejb.processAddToCart(null, null);
        assertEquals("No product selected.", result);
        System.out.println("FIXED: Validation tested directly on EJB.");
        System.out.println("No HTTP request needed. SRP respected.");
    }

    /**
     * TEST: Invalid format handled cleanly in Business Layer
     */
    @Test
    public void testInvalidFormatReturnsError() {
        MiniShopEJB ejb = new MiniShopEJB();
        String result = ejb.processAddToCart("abc", null);
        assertEquals("Invalid product ID format.", result);
        System.out.println("FIXED: Format error handled in Business Layer.");
    }

    /**
     * TEST: Empty product returns error
     */
    @Test
    public void testEmptyProductReturnsError() {
        MiniShopEJB ejb = new MiniShopEJB();
        String result = ejb.processAddToCart("", null);
        assertEquals("No product selected.", result);
        System.out.println("FIXED: Empty input handled in Business Layer.");
    }
}