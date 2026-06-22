package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.ConfirmationCode;
import de.ait.g_67_shop.domain.User;
import de.ait.g_67_shop.repository.ConfirmationCodeRepository;
import de.ait.g_67_shop.service.interfaces.ConfirmationCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    private final ConfirmationCodeRepository repository;

    public ConfirmationCodeServiceImpl(ConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generateConfirmationCode(User user) {
        String codeValue = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusHours(24);
        ConfirmationCode entity = new ConfirmationCode(codeValue, expiration, user);
        repository.save(entity);
        return codeValue;
    }
}
