package test;

import entity.Product;
import validation.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * After applying the pipeline pattern,
 * each validation stage can be tested independently.
 * No EJB container, no GlassFish, no HTTP needed.
 */
public class ValidationPipelineFixedTest {

    // Helper: creates a valid available product
    private Product makeProduct(int available, int stock) {
        Product p = new Product();
        p.setAvailable(available);
        p.setQuantityOnHand(stock);
        p.setDescription("Test Product");
        p.setPurchaseCost(1095.0);
        return p;
    }

    /**
     * TEST 1: NullCheckValidator blocks null input.
     * Tested directly — no HTTP needed.
     */
    @Test
    public void testNullCheckValidatorBlocksNull() {
        Validator v = new NullCheckValidator();
        String error = v.validate(null, null);
        assertEquals("No product selected.", error);
        System.out.println("FIXED: NullCheckValidator tested independently.");
        System.out.println("Result: " + error);
    }

    /**
     * TEST 2: AvailabilityValidator blocks out-of-stock product.
     * Tested directly on the stage — no pipeline needed.
     */
    @Test
    public void testAvailabilityValidatorBlocksOutOfStock() {
        Validator v = new AvailabilityValidator();
        Product outOfStock = makeProduct(1, 0); // available but 0 stock
        String error = v.validate("980001", outOfStock);
        assertNotNull(error);
        assertTrue(error.contains("out of stock"));
        System.out.println("FIXED: AvailabilityValidator blocks stock=0.");
        System.out.println("Result: " + error);
    }

    /**
 * TEST 3: Full pipeline passes a valid product cleanly.
 */
    @Test
    public void testPipelinePassesValidProduct() {
    Product valid = makeProduct(1, 100);

    // Stage 1: null check
    Validator nullCheck = new NullCheckValidator();
    assertNull(nullCheck.validate("980001", null));

    // Stage 2: format check
    Validator formatCheck = new FormatValidator();
    assertNull(formatCheck.validate("980001", null));

    // Stage 3: existence check
    Validator existenceCheck = new ExistenceValidator();
    assertNull(existenceCheck.validate("980001", valid));

    // Stage 4: availability check
    Validator availabilityCheck = new AvailabilityValidator();
    assertNull(availabilityCheck.validate("980001", valid));

    System.out.println("FIXED: All 4 pipeline stages passed independently.");
    System.out.println("Each stage tested directly. No GlassFish needed.");
    System.out.println("Safe to add to cart.");
}
}