package de.ait.g_67_shop.security.service;

import de.ait.g_67_shop.exceptions.types.AuthorizationException;
import de.ait.g_67_shop.security.dto.LoginRequestDto;
import de.ait.g_67_shop.security.dto.TokenResponseDto;
import de.ait.g_67_shop.service.interfaces.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final Map<String, String> refreshStorage;

    public AuthService(UserService userService, BCryptPasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        refreshStorage = new ConcurrentHashMap<>();
    }

    public TokenResponseDto login(LoginRequestDto requestDto) {
        String email = requestDto.getEmail();
        UserDetails user = userService.loadUserByUsername(email);

        if (passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            String accessToken = tokenService.generateAccessToken(email);
            String refreshToken = tokenService.generateRefreshToken(email);
            refreshStorage.put(email, refreshToken);
            return new TokenResponseDto(accessToken, refreshToken);
        } else {
            throw new AuthorizationException("Password is incorrect");
        }

    }

    public TokenResponseDto getAccessToken(HttpServletRequest request) {
        String refreshToken = tokenService.getTokenFromRequest(request, "Refresh-Token");

        if (refreshToken != null && tokenService.validateRefreshToken(refreshToken)) {
            Claims refreshClaims = tokenService.getRefreshClaims(refreshToken);
            String email = refreshClaims.getSubject();
            String savedRefreshToken = refreshStorage.get(email);

            if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
                String accessToken = tokenService.generateAccessToken(email);
                return new TokenResponseDto(accessToken);
            }
        }

        throw new AuthorizationException("Refresh token is invalid");
    }

    public void removeUserRefreshToken(HttpServletRequest request) {
        String refreshToken = tokenService.getTokenFromRequest(request, "Refresh-Token");

        if (refreshToken != null && tokenService.validateRefreshToken(refreshToken)) {
            Claims refreshClaims = tokenService.getRefreshClaims(refreshToken);
            String email = refreshClaims.getSubject();
            refreshStorage.remove(email);
        }
    }
}
