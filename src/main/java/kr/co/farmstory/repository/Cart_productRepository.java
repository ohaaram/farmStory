package kr.co.farmstory.repository;

import kr.co.farmstory.entity.Cart_product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Cart_productRepository extends JpaRepository<Cart_product, Integer> {
}
