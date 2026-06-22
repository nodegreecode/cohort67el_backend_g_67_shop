package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.dto.user.UserRegistrationDto;
import de.ait.g_67_shop.service.interfaces.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRegistrationDto registrationDto) {
        userService.register(registrationDto);
        return "Registration complete. Please check your email.";
    }
}
