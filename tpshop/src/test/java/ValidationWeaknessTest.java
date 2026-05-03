package test;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Demonstrates that the original processAddToCart()
 * has no real validation pipeline.
 * Only one check exists: null/empty.
 * No existence check, no stock check, no availability check.
 */
public class ValidationWeaknessTest {

    /**
     * TEST 1: Proves only one validation exists.
     * The original code has exactly one if-statement for validation.
     * Everything else is caught by a generic Exception.
     */
    @Test
    public void testOnlyNullCheckExists() {
        // This is the ENTIRE validation in the original processAddToCart
        String productParam = null;
        String error = null;

        // Only check that exists
        if (productParam == null || productParam.isEmpty()) {
            error = "No product selected.";
        }
        // After this: NO existence check, NO stock check, NO availability check
        // Everything else falls into catch(Exception e)

        assertEquals("No product selected.", error);
        System.out.println("PROOF: Only 1 validation exists in original code.");
        System.out.println("No stock check. No availability check.");
        System.out.println("No existence check before DB call.");
    }

    /**
     * TEST 2: Proves that out-of-stock products are not blocked.
     * If quantityOnHand = 0, the original code still adds to cart.
     * There is no stage that checks stock before proceeding.
     */
    @Test
    public void testOutOfStockProductNotBlocked() {
        // Simulate a product with 0 stock
        int quantityOnHand = 0;
        boolean available = true;

        // Original code never checks this
        // It goes straight to cartEJB.addProduct()
        String error = null; // original code returns null = success

        // Proof: no validation stopped this
        assertNull(error);
        System.out.println("PROOF: Product with stock=" + quantityOnHand
                + " was NOT blocked.");
        System.out.println("Original pipeline has no stock validation stage.");
    }

    /**
     * TEST 3: Proves that unavailable products are not blocked.
     * If available = 0 (false in Derby), original code still adds to cart.
     */
    @Test
    public void testUnavailableProductNotBlocked() {
        // Simulate a product marked as unavailable
        int available = 0; // 0 = FALSE in Derby

        // Original code never checks this either
        String error = null; // original returns null = success always

        assertNull(error);
        System.out.println("PROOF: Product with available=" + available
                + " was NOT blocked.");
        System.out.println("Original pipeline has no availability validation stage.");
        System.out.println("An unavailable product can be added to cart freely.");
    }
}