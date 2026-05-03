package entity;

import java.io.Serializable;

/**
 * Value Object representing one item in the shopping cart.
 * Replaces the primitive String representation.
 * 
 * Pattern: Value Object / Domain Model
 */
public class CartItem implements Serializable {

    private Long productId;
    private double price;
    private int quantity;

    public CartItem(Long productId, double price) {
        this.productId = productId;
        this.price = price;
        this.quantity = 1;
    }

    // Increment quantity instead of adding duplicate
    public void incrementQuantity() {
        this.quantity++;
    }

    public double getSubtotal() {
        return price * quantity;
    }

    public Long getProductId() { return productId; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return "Product ID: " + productId 
             + " | Price: " + price 
             + "$ | Quantity: " + quantity;
    }
}