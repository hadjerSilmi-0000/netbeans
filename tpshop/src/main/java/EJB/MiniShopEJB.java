package EJB;

import dao.ProductDAO;
import entity.Product;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import validation.ValidationPipeline;

@Stateless
public class MiniShopEJB {

    @EJB
    private ProductDAO productDAO;

    private double lastPrice = 0;

    public String processAddToCart(String productParam, MiniShopFull cartEJB) {

    ValidationPipeline pipeline = new ValidationPipeline();

    // Stage 1 + 2: null and format check only
    String earlyError = pipeline.runEarly(productParam);
    if (earlyError != null) {
        return earlyError;
    }

    // Safe to parse and load product
    Long productId = Long.parseLong(productParam);
    Product p = null;
    try {
        p = productDAO.findById(productId);
    } catch (Exception e) {
        // Show real error instead of hiding it
        return "REAL ERROR: " + e.getClass().getName() 
             + " --- " + e.getMessage();
    }

    // Stage 3 + 4: existence and availability check
    String lateError = pipeline.runLate(productParam, p);
    if (lateError != null) {
        return lateError;
    }

    // All stages passed
    this.lastPrice = p.getPurchaseCost();
    cartEJB.addProduct(productId, lastPrice);
    return null;
}
    public double getPrice(Long productId) {
        Product p = productDAO.findById(productId);
        return p.getPurchaseCost();
    }

    public double getLastPrice() {
        return lastPrice;
    }
}