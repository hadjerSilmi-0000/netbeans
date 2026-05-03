package validation;

import entity.Product;

/**
 * Stage 2: checks if input is a valid Long.
 */
public class FormatValidator implements Validator {

    @Override
    public String validate(String productParam, Product product) {
        try {
            Long.parseLong(productParam);
            return null;
        } catch (NumberFormatException e) {
            return "Invalid product ID format.";
        }
    }
}