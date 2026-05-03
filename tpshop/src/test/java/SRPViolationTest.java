

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test class DEMONSTRATES the SRP violation in MiniShopServFull.
 * 
 * PROBLEM: We cannot test the validation logic independently
 * because it is locked inside the Servlet.
 * 
 * To test "what happens when product is null", we would need
 * a full HTTP request, a running GlassFish server, and a deployed WAR.
 * That is the proof that SRP is violated.
 */
public class SRPViolationTest {

    /**
     * TEST 1: Demonstrates that validation logic cannot be tested
     * without an HTTP environment.
     * 
     * In the ORIGINAL code, this logic lives inside the Servlet:
     * 
     *   if (productParam == null || productParam.isEmpty()) {
     *       error = "Aucun produit selectionne.";
     *   }
     * 
     * We CANNOT call this directly. We cannot write:
     *   servlet.validateProduct(null)
     * because no such method exists.
     * 
     * CONCLUSION: Business logic trapped in Controller = untestable = SRP violation.
     */
    @Test
    public void testValidationIsTrappedInServlet() {
        // Simulate what SHOULD be testable independently
        String productParam = null;
        String error = null;

        // This is the logic currently STUCK inside the Servlet
        // We have to copy it here just to test it
        // That proves it does not belong there
        if (productParam == null || productParam.isEmpty()) {
            error = "No product selected.";
        }

        // This works here but CANNOT be tested through the Servlet directly
        assertEquals("No product selected.", error);
        
        System.out.println("PROOF: Validation logic had to be copied out of");
        System.out.println("the Servlet to be tested. This proves SRP violation.");
        System.out.println("If SRP was respected, we would call: ejb.validate(null)");
    }

    /**
     * TEST 2: Demonstrates that parsing logic cannot be tested
     * without an HTTP environment.
     * 
     * In the ORIGINAL code, this logic lives inside the Servlet:
     * 
     *   Long productId = Long.parseLong(productParam);
     * 
     * If someone sends "abc" instead of a number,
     * the Servlet crashes. There is no clean way to test this
     * without sending a real HTTP request.
     */
    @Test
    public void testParsingIsTrappedInServlet() {
        String badInput = "abc";
        
        try {
            // This is the parsing STUCK inside the Servlet
            Long productId = Long.parseLong(badInput);
            fail("Should have thrown exception");
        } catch (NumberFormatException e) {
            // In the original code this exception is caught by a generic
            // catch(Exception e) inside the Servlet
            // There is no specific handling, no proper error message
            System.out.println("PROOF: NumberFormatException caught generically.");
            System.out.println("No specific handling because parsing is in the wrong layer.");
            assertTrue(true);
        }
    }

    /**
     * TEST 3: Demonstrates the coupling problem.
     * 
     * In the ORIGINAL code, changing the error message from French
     * to English requires opening MiniShopServFull.java
     * which is a Controller class, not a business class.
     * 
     * Changing a BUSINESS RULE (what counts as invalid input)
     * forces us to modify a WEB LAYER class.
     * That is the definition of SRP violation.
     */
    @Test
    public void testBusinessRuleChangeRequiresServletModification() {
        // Document the current state
        String currentErrorMessage = "Aucun produit selectionne.";
        String layer = "MiniShopServFull (Servlet - Web Layer)";
        
        System.out.println("PROOF OF SRP VIOLATION:");
        System.out.println("Business rule (error message): " + currentErrorMessage);
        System.out.println("Location of this rule: " + layer);
        System.out.println("To change this business rule, developer must");
        System.out.println("modify a Web Layer Controller class.");
        System.out.println("This violates SRP.");
        
        // This assertion documents the architectural problem
        assertEquals(
            "Web Layer should NOT contain business messages",
            "MiniShopServFull (Servlet - Web Layer)",
            layer
        );
    }
}