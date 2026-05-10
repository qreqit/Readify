package mate.academy.springbootwebgreqit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootwebgreqit.dto.cartitem.CartItemRequestDto;
import mate.academy.springbootwebgreqit.dto.shoppingcart.UpdateCartItemDto;
import mate.academy.springbootwebgreqit.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootwebgreqit.service.ShoppingCartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get shopping cart for current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shopping cart retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{shoppingCartId}")
    public ShoppingCartDto getShoppingCartForCurrentUser(Authentication authentication,
                                                         @PathVariable Long shoppingCartId) {
        return shoppingCartService.getShoppingCartForCurrentUser(authentication, shoppingCartId);
    }

    @Operation(summary = "Add book to shopping cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book added to shopping cart"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ShoppingCartDto addBookToShoppingCart(@RequestBody CartItemRequestDto cartItem,
                                                 Authentication authentication) {
        return shoppingCartService.addBookToShoppingCart(cartItem, authentication);
    }

    @Operation(summary = "Update cart item quantity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart item quantity updated successfully"),
        @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartDto updateCartItemQuantity(@PathVariable Long cartItemId,
                                                  @RequestBody @Valid UpdateCartItemDto quantity) {
        return shoppingCartService.updateCartItemQuantity(cartItemId, quantity);
    }

    @Operation(summary = "Remove cart item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cart item removed successfully"),
        @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/items/{cartItemId}")
    public void removeCartItem(@PathVariable Long cartItemId, Authentication authentication) {
        shoppingCartService.removeCartItem(cartItemId, authentication);
    }
}
