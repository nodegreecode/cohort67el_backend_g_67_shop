package de.ait.g_67_shop.repository;

import de.ait.g_67_shop.domain.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {
}
