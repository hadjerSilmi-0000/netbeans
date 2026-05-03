package dao;

import entity.Product;
import java.util.List;

public interface ProductDAO {
    Product findById(Long id);
    List<Product> findAll();
    void updateStock(Long productId, int newQuantity);
    
    
}


