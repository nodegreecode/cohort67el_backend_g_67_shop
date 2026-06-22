package de.ait.g_67_shop.service.interfaces;

import de.ait.g_67_shop.domain.User;

public interface ConfirmationCodeService {
    String generateConfirmationCode(User user);
}
