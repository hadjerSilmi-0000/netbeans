package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PRODUCT", schema = "APP")
public class Product {

    @Id
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Column(name = "PURCHASE_COST")
    private double purchaseCost;

    @Column(name = "QUANTITY_ON_HAND")
    private int quantityOnHand;

    @Column(name = "AVAILABLE")
    private int available;

    @Column(name = "DESCRIPTION")
    private String description;

    public Long getId() { return id; }
    public double getPurchaseCost() { return purchaseCost; }
    public int getQuantityOnHand() { return quantityOnHand; }
    public int getAvailable() { return available; }
    public String getDescription() { return description; }

    public void setId(Long id) { this.id = id; }
    public void setPurchaseCost(double p) { this.purchaseCost = p; }
    public void setQuantityOnHand(int q) { this.quantityOnHand = q; }
    public void setAvailable(int a) { this.available = a; }
    public void setDescription(String d) { this.description = d; }
}