package dao;

import entity.Product;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 * Full Repository implementation.
 * IMPROVED: implements all ProductDAO methods.
 * 
 * Pattern: Repository Pattern
 */
@Stateless
public class ProductDAOImpl implements ProductDAO {

    @PersistenceContext(unitName = "MiniShopPU")
    private EntityManager em;

    /*@Override
    public Product findById(Long id) {
        Product p = em.find(Product.class, id);
        if (p == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        return p;
    }*/
    @Override
public Product findById(Long id) {
    System.out.println("Looking for product ID: " + id);
    Product p = em.find(Product.class, id);
    System.out.println("Found: " + p);
    if (p == null) {
        throw new IllegalArgumentException("Product not found: " + id);
    }
    return p;
}

    @Override
    public List<Product> findAll() {
        return em.createQuery(
            "SELECT p FROM Product p", Product.class)
            .getResultList();
    }

    @Override
    public void updateStock(Long productId, int newQuantity) {
        Product p = findById(productId);
        p.setQuantityOnHand(newQuantity);
        em.merge(p);
    }
}

