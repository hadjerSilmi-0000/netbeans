package validation;

import entity.Product;

/**
 * Stage 1: checks if input is null or empty.
 * Product is null at this stage — not yet loaded.
 */
public class NullCheckValidator implements Validator {

    @Override
    public String validate(String productParam, Product product) {
        if (productParam == null || productParam.isEmpty()) {
            return "No product selected.";
        }
        return null;
    }
}