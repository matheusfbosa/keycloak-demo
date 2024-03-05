package com.github.matheusfbosa.keycloakdemo.product;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

  private final JdbcClient jdbcClient;

  public ProductRepository(final JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  public List<Product> getAll() {
    return jdbcClient.sql("SELECT * FROM products")
        .query(Product.class)
        .list();
  }

  public Product create(final Product product) {
    final var sql = """
        INSERT INTO products(title, description) VALUES (:title, :description) RETURNING id;
        """;
    final KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcClient.sql(sql)
        .param("title", product.title())
        .param("description", product.description())
        .update(keyHolder);
    final var id = keyHolder.getKeyAs(Long.class);
    return new Product(id, product.title(), product.description());
  }
}
