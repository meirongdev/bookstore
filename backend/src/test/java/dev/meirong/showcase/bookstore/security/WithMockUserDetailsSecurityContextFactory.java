package dev.meirong.showcase.bookstore.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.entities.Role;
import dev.meirong.showcase.bookstore.security.entities.CustomUserDetails;

/**
 * Factory class that creates a SecurityContext with a mock UserDetails for testing.
 * This is used by the @WithMockUserDetails annotation to populate the SecurityContext
 * with the correct UserDetails instance during tests.
 */
public class WithMockUserDetailsSecurityContextFactory
        implements WithSecurityContextFactory<WithMockUserDetails> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserDetails annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create a mock User entity
        User mockUser = new User();
        mockUser.setId(annotation.id());
        mockUser.setEmail(annotation.username());
        mockUser.setRole(Role.valueOf(annotation.role()));

        // Wrap it in UserDetails
        CustomUserDetails userDetails = new CustomUserDetails(mockUser);

        // Create authentication token with UserDetails as principal
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                "password",
                userDetails.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }
}
