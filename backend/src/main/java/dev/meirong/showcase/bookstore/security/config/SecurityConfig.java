package dev.meirong.showcase.bookstore.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dev.meirong.showcase.bookstore.security.jwt.JwtAuthenticationFilter;
import dev.meirong.showcase.bookstore.security.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            return http
                    // Disable csrf
                    .csrf(csrf -> csrf.disable())

                    // Protect endpoints at /api/<type>/secure
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers(
                                    "/api/books/secure/**",
                                    "/api/checkouts/secure/**",
                                    "/api/reviews/secure/**",
                                    "/api/discussions/secure/**",
                                    "/api/payment/secure/**",
                                    "/api/admin/secure/**",
                                    "/api/upload/secure/**"
                            ).authenticated()
                            .anyRequest().permitAll()
                    )

                    // Configure session management
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )

                    // Add authentication provider
                    .authenticationProvider(authenticationProvider())

                    // Add JWT Filter
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                    // Add CORS filters
                    // .cors(Customizer.withDefaults())

                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure security", e);
        }
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        try {
            return authConfig.getAuthenticationManager();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get authentication manager", e);
        }
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


}
