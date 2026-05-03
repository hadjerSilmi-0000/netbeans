package validation;

import entity.Product;

/**
 * Stage 3: checks if product exists in database.
 * Product is passed in after being loaded by EJB.
 */
public class ExistenceValidator implements Validator {

    @Override
    public String validate(String productParam, Product product) {
        if (product == null) {
            return "Product does not exist: " + productParam;
        }
        return null;
    }
}