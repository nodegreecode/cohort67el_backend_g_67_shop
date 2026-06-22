package de.ait.g_67_shop.repository;

import de.ait.g_67_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByActiveTrue();

    Optional<Product> findByIdAndActiveTrue(Long id);

    long countByActiveTrue();
}
