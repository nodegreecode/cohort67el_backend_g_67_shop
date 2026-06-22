package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.User;
import de.ait.g_67_shop.domain.enums.Role;
import de.ait.g_67_shop.dto.user.UserRegistrationDto;
import de.ait.g_67_shop.exceptions.types.RegistrationException;
import de.ait.g_67_shop.repository.UserRepository;
import de.ait.g_67_shop.security.AuthUserDetails;
import de.ait.g_67_shop.service.interfaces.EmailService;
import de.ait.g_67_shop.service.interfaces.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository repository,
                           BCryptPasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with email %s not found", email)
                )
        );
        return new AuthUserDetails(user);
    }

    @Override
    public void register(UserRegistrationDto userRegistrationDto) {
        String email = userRegistrationDto.getEmail();
        String name = userRegistrationDto.getName();
        String password = userRegistrationDto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        User user = repository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setConfirmed(false);
            user.setRole(Role.ROLE_USER);
        } else if (user.isConfirmed()) {
            throw new RegistrationException(String.format("Email %s already in use", email));
        }

        user.setPassword(encodedPassword);
        user.setName(name);

        repository.save(user);

        emailService.sendConfirmationEmail(user);
    }
}
