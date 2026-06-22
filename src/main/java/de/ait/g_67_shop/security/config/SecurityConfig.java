package de.ait.g_67_shop.security.config;

import de.ait.g_67_shop.security.filter.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, TokenFilter filter) {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //.httpBasic(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(x -> x
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/{id:\\d+}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/products/{id:\\d+}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/{id:\\d+}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/{id:\\d+}/restore").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/products/count").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/products/total-cost").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/products/avg-price").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products/{id:\\d+}/image").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/customers/{customerId:\\d+}/positions/{productId:\\d+}").hasAnyRole("ADMIN", "USER")

                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/access").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/logout").permitAll()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
