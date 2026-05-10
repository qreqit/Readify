package mate.academy.springbootwebgreqit.service;

import mate.academy.springbootwebgreqit.dto.cartitem.CartItemRequestDto;
import mate.academy.springbootwebgreqit.dto.shoppingcart.UpdateCartItemDto;
import mate.academy.springbootwebgreqit.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootwebgreqit.model.ShoppingCart;
import mate.academy.springbootwebgreqit.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartForCurrentUser(Authentication authentication,
                                                  Long shoppingCartId);

    ShoppingCartDto addBookToShoppingCart(CartItemRequestDto cartItem,
                                          Authentication authentication);

    ShoppingCartDto updateCartItemQuantity(Long cartItemId,
                                           UpdateCartItemDto quantity);

    ShoppingCartDto removeCartItem(Long cartItemId,
                                   Authentication authentication);

    ShoppingCart createShoppingCart(User user);
}
