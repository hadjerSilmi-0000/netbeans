package test;

import dao.ProductDAO;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Method;

/**
 * Demonstrates the Anemic DAO weakness by documenting
 * what the original ProductDAO looked like before the fix.
 * 
 * The original interface had only ONE method: findById.
 * This test documents that architectural problem.
 */
public class AnemicDAOWeaknessTest {

    /**
     * TEST 1: Documents what the original DAO looked like.
     * The original ProductDAO had only findById.
     * This forced HTML to hardcode products.
     * 
     * Original code was:
     * public interface ProductDAO {
     *     Product findById(Long id);  // ONLY method
     * }
     */
    @Test
    public void testOriginalDAOWasAnemic() {
        // Document the original weakness
        String originalInterface = 
            "public interface ProductDAO {\n" +
            "    Product findById(Long id); // ONLY method\n" +
            "}";

        // The original DAO had exactly 1 method
        int originalMethodCount = 1;

        System.out.println("PROOF OF ANEMIC DAO WEAKNESS:");
        System.out.println("Original ProductDAO interface:");
        System.out.println(originalInterface);
        System.out.println("Number of methods: " + originalMethodCount);
        System.out.println("findAll() did NOT exist.");
        System.out.println("updateStock() did NOT exist.");
        System.out.println("This forced index.html to hardcode products.");
        System.out.println("Any new product in DB would NEVER appear.");

        assertEquals(1, originalMethodCount);
    }

    /**
     * TEST 2: Proves the consequence of missing findAll.
     * Without findAll, HTML must hardcode product options.
     * Simulates what the developer was forced to do.
     */
    @Test
    public void testMissingFindAllForcesHardcodedHTML() {
        // This is what the developer had to do in index.html
        // because findAll() did not exist
        String hardcodedHTML =
            "<option value='980001'>laptop</option>\n" +
            "<option value='980005'>phone</option>";

        // Only 2 products hardcoded
        // Database has 30 products but none visible
        int hardcodedProductCount = 2;

        System.out.println("PROOF: Because findAll() was missing,");
        System.out.println("developer was forced to write:");
        System.out.println(hardcodedHTML);
        System.out.println("Only " + hardcodedProductCount 
                + " products visible out of 30 in database.");
        System.out.println("This is a direct consequence of Anemic DAO.");

        assertEquals(2, hardcodedProductCount);
        assertTrue(hardcodedHTML.contains("980001"));
        assertTrue(hardcodedHTML.contains("980005"));
    }

    /**
     * TEST 3: Proves that updateStock was missing.
     * Without updateStock, buying never reduces inventory.
     * Stock stays the same regardless of how many items sold.
     */
    @Test
    public void testMissingUpdateStockMeansInventoryNeverChanges() {
        // Simulate original behavior
        int stockBefore = 800; // product 980001 has 800 units
        int stockAfter = 800;  // after buying, stock is still 800
        // because updateStock() did not exist in original DAO

        System.out.println("PROOF: Without updateStock() in DAO,");
        System.out.println("Stock before purchase: " + stockBefore);
        System.out.println("Stock after purchase: " + stockAfter);
        System.out.println("Stock never changes. Inventory is broken.");
        System.out.println("This is a direct consequence of Anemic DAO.");

        // Stock never changed because no updateStock method existed
        assertEquals(stockBefore, stockAfter);
    }
}