package dev.meirong.showcase.bookstore.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Custom annotation to inject a mock UserDetails into the SecurityContext for testing.
 * This annotation allows testing of endpoints that use @AuthenticationPrincipal UserDetails.
 *
 * Usage:
 * <pre>
 * {@code @Test}
 * {@code @WithMockUserDetails(username = "test@example.com", role = "ROLE_USER")}
 * void testSecureEndpoint() {
 *     // Test code that requires authentication
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserDetailsSecurityContextFactory.class)
public @interface WithMockUserDetails {

    /**
     * The email/username of the mock user
     */
    String username() default "user@test.com";

    /**
     * The role of the mock user (e.g., "ROLE_USER", "ROLE_ADMIN")
     */
    String role() default "ROLE_USER";

    /**
     * The ID of the mock user
     */
    long id() default 1L;
}
