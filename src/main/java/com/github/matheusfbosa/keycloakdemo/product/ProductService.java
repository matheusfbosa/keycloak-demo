package com.github.matheusfbosa.keycloakdemo.product;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(final ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<Product> getAllProducts() {
    return productRepository.getAll();
  }

  public Product createProduct(final Product product) {
    return productRepository.create(product);
  }
}
