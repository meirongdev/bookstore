package dev.meirong.showcase.bookstore.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import dev.meirong.showcase.bookstore.dto.BookDTO;
import dev.meirong.showcase.bookstore.dto.CheckoutDTO;
import dev.meirong.showcase.bookstore.security.WithMockUserDetails;
import dev.meirong.showcase.bookstore.services.CheckoutService;

/**
 * Unit tests for CheckoutController using @WithMockUser to test @AuthenticationPrincipal
 *
 * This demonstrates how Spring Security's @AuthenticationPrincipal works with testing.
 * @WithMockUser automatically creates a mock authenticated user that gets injected
 * into controller methods annotated with @AuthenticationPrincipal.
 *
 * Note: Using @MockitoBean (Spring Framework 6.2+) instead of deprecated @MockBean
 */
@SpringBootTest
class CheckoutControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should get current checkouts for authenticated user")
    @WithMockUserDetails(username = "user@test.com")
    void testGetCurrentCheckouts() throws Exception {
        // Arrange
        BookDTO bookDTO1 = new BookDTO();
        bookDTO1.setId(1L);
        bookDTO1.setTitle("Test Book 1");

        CheckoutDTO checkout1 = new CheckoutDTO();
        checkout1.setBookDTO(bookDTO1);
        checkout1.setDaysLeft(7);

        BookDTO bookDTO2 = new BookDTO();
        bookDTO2.setId(2L);
        bookDTO2.setTitle("Test Book 2");

        CheckoutDTO checkout2 = new CheckoutDTO();
        checkout2.setBookDTO(bookDTO2);
        checkout2.setDaysLeft(5);

        List<CheckoutDTO> checkouts = List.of(checkout1, checkout2);

        when(checkoutService.getCurrentCheckouts(anyString())).thenReturn(checkouts);

        // Act & Assert
        mockMvc.perform(get("/api/checkouts/secure/current-checkouts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].bookDTO.id").value(1))
                .andExpect(jsonPath("$[0].bookDTO.title").value("Test Book 1"))
                .andExpect(jsonPath("$[0].daysLeft").value(7))
                .andExpect(jsonPath("$[1].bookDTO.id").value(2))
                .andExpect(jsonPath("$[1].daysLeft").value(5));

        // Verify service was called with correct email
        verify(checkoutService).getCurrentCheckouts("user@test.com");
    }

    @Test
    @DisplayName("Should get current checkouts count for authenticated user")
    @WithMockUserDetails(username = "user@test.com")
    void testGetCurrentCheckoutsCount() throws Exception {
        // Arrange
        Integer expectedCount = 3;
        when(checkoutService.getCurrentCheckoutsCount(anyString())).thenReturn(expectedCount);

        // Act & Assert
        mockMvc.perform(get("/api/checkouts/secure/current-loans-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(3));

        // Verify service was called with correct email
        verify(checkoutService).getCurrentCheckoutsCount("user@test.com");
    }

    @Test
    @DisplayName("Should return 403 when accessing current checkouts without authentication")
    void testGetCurrentCheckoutsWithoutAuth() throws Exception {
        // Act & Assert - Spring Security returns 403 (Forbidden) for unauthenticated requests
        mockMvc.perform(get("/api/checkouts/secure/current-checkouts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 when accessing checkouts count without authentication")
    void testGetCurrentCheckoutsCountWithoutAuth() throws Exception {
        // Act & Assert - Spring Security returns 403 (Forbidden) for unauthenticated requests
        mockMvc.perform(get("/api/checkouts/secure/current-loans-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should handle empty checkouts list")
    @WithMockUserDetails(username = "user@test.com")
    void testGetCurrentCheckouts_Empty() throws Exception {
        // Arrange
        when(checkoutService.getCurrentCheckouts(anyString())).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/checkouts/secure/current-checkouts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(checkoutService).getCurrentCheckouts("user@test.com");
    }

    @Test
    @DisplayName("Should handle zero checkouts count")
    @WithMockUserDetails(username = "user@test.com")
    void testGetCurrentCheckoutsCount_Zero() throws Exception {
        // Arrange
        when(checkoutService.getCurrentCheckoutsCount(anyString())).thenReturn(0);

        // Act & Assert
        mockMvc.perform(get("/api/checkouts/secure/current-loans-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));

        verify(checkoutService).getCurrentCheckoutsCount("user@test.com");
    }
}
