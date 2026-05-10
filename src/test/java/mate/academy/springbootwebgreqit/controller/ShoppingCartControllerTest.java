package mate.academy.springbootwebgreqit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.springbootwebgreqit.dto.cartitem.CartItemRequestDto;
import mate.academy.springbootwebgreqit.dto.shoppingcart.UpdateCartItemDto;
import mate.academy.springbootwebgreqit.ratelimit.BookRateLimiterService;
import mate.academy.springbootwebgreqit.security.CustomUserDetailService;
import mate.academy.springbootwebgreqit.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;

    @MockBean
    private BookRateLimiterService bookRateLimiterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }


    @Test
    @Sql(scripts = {"/data-sql/create-books.sql",
            "/data-sql/create-users.sql", "/data-sql/create-cart-item.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables-for-sh.sql", executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "john.doe@example.com", roles = {"USER"})
    @DisplayName("Get shopping cart for the current user")
    void getShoppingCart_ShouldReturnCartForUser() throws Exception {
        Long shoppingCartId = 1L;
        mockMvc.perform(get("/cart/{shoppingCartId}", shoppingCartId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1L))
                .andReturn();
    }

    @Test
    @Sql(scripts = {"/data-sql/create-books.sql", "/data-sql/create-users.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables-for-sh.sql", executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "john.doe@example.com", roles = {"USER"})
    @DisplayName("Add book to shopping cart")
    void addBookToShoppingCart_ShouldReturnUpdatedCart() throws Exception {
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setBookId(1L);
        cartItemRequestDto.setQuantity(2);

        String jsonRequest = objectMapper.writeValueAsString(cartItemRequestDto);

        MvcResult result = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems[0].quantity")
                        .value(cartItemRequestDto.getQuantity()))
                .andReturn();
    }


    @Test
    @Sql(scripts = {"/data-sql/create-books.sql",
            "/data-sql/create-users.sql", "/data-sql/create-cart-item.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables-for-sh.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "john.doe@example.com", roles = {"USER"})
    @DisplayName("Update cart item quantity")
    void updateCartItemQuantity_ShouldReturnUpdatedQuantity() throws Exception {
        Long cartItemId = 1L;
        UpdateCartItemDto quantityDto = new UpdateCartItemDto();
        quantityDto.setQuantity(3);

        String jsonRequest = objectMapper.writeValueAsString(quantityDto);

        mockMvc.perform(put("/cart/items/{cartItemId}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems[0].quantity").value(quantityDto.getQuantity()))
                .andReturn();
    }

    @Test
    @Sql(scripts = {"/data-sql/create-books.sql", "/data-sql/create-users.sql",
            "/data-sql/create-cart-item.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables-for-sh.sql", executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "john.doe@example.com", roles = {"USER"})
    @DisplayName("Remove item from cart")
    void removeItemFromCart_ShouldReturnEmptyCart() throws Exception {
        Long cartItemId = 1L;

        mockMvc.perform(delete("/cart/items/{cartItemId}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
