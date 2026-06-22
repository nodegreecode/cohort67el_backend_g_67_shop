package de.ait.g_67_shop.security.controller;

import de.ait.g_67_shop.security.dto.LoginRequestDto;
import de.ait.g_67_shop.security.dto.TokenResponseDto;
import de.ait.g_67_shop.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        TokenResponseDto tokens = authService.login(requestDto);

        Cookie accessCookie = new Cookie("Access-Token", tokens.getAccessToken());
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("Refresh-Token", tokens.getRefreshToken());
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
    }

    @PostMapping("/access")
    public void getNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokens = authService.getAccessToken(request);

        Cookie accessCookie = new Cookie("Access-Token", tokens.getAccessToken());
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        response.addCookie(accessCookie);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.removeUserRefreshToken(request);

        Cookie accessCookie = new Cookie("Access-Token", null);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("Refresh-Token", null);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    @GetMapping("/csrf")
    public CsrfToken getCsrfToken(CsrfToken csrfToken) {
        return csrfToken;
    }
}
