package sample.market.infrastructure.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sample.market.domain.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
