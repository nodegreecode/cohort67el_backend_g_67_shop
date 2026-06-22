package de.ait.g_67_shop.service.interfaces;

import de.ait.g_67_shop.dto.user.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void register(UserRegistrationDto userRegistrationDto);

}
