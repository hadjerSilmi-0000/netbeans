package validation;

import entity.Product;

/**
 * Pipeline stage interface.
 * Each validator does exactly one check.
 * Pattern: Chain of Responsibility
 */
public interface Validator {
    String validate(String productParam, Product product);
}