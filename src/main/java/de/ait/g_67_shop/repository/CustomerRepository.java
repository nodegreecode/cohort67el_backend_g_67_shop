package de.ait.g_67_shop.repository;

import de.ait.g_67_shop.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndActiveTrue(Long id);

    List<Customer> findAllByActiveTrue();

    long countByActiveTrue();
}
