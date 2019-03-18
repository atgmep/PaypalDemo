package nghia.firsttask.service;

import nghia.firsttask.entity.Product;
import nghia.firsttask.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final int STATUS_SHOW = 1;
    private final int STATUS_HIDE = -1;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findByStatus(STATUS_SHOW);
    }

    public Product createProduct(String name, Double price) {
        Product productEntity = new Product();
        productEntity.setName(name);
        productEntity.setPrice(price);
        productEntity.setStatus(STATUS_SHOW);
        productRepository.save(productEntity);
        return productEntity;
    }

    public boolean updateProduct(Integer editProductId, String name, Double price) {
        Optional<Product> optional = productRepository.findById(editProductId);
        if (optional.isPresent()) {
            Product productEntity = optional.get();
            productEntity.setName(name);
            productEntity.setPrice(price);
            productRepository.save(productEntity);
            return true;
        } else {
            return false;
        }
    }

    public boolean hideProduct(Integer deleteProductId) {
        Optional<Product> optional = productRepository.findById(deleteProductId);
        if (optional.isPresent()) {
            Product productEntity = optional.get();
            productEntity.setStatus(STATUS_HIDE);
            productRepository.save(productEntity);
            return true;
        } else {
            return false;
        }
    }
}
