package validation;

import entity.Product;

/**
 * Stage 4: checks stock and availability flag.
 * available = 1 means TRUE in Derby.
 */
public class AvailabilityValidator implements Validator {

    @Override
    public String validate(String productParam, Product product) {
        if (product.getAvailable() != 1) {
            return "Product is not available: " + product.getDescription();
        }
        if (product.getQuantityOnHand() <= 0) {
            return "Product is out of stock: " + product.getDescription();
        }
        return null;
    }
}