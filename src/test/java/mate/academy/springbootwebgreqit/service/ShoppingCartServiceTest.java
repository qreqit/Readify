package mate.academy.springbootwebgreqit.service;

import mate.academy.springbootwebgreqit.dto.cartitem.CartItemRequestDto;
import mate.academy.springbootwebgreqit.dto.shoppingcart.UpdateCartItemDto;
import mate.academy.springbootwebgreqit.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootwebgreqit.exception.EntityNotFoundException;
import mate.academy.springbootwebgreqit.mapper.CartItemMapper;
import mate.academy.springbootwebgreqit.mapper.ShoppingCartMapper;
import mate.academy.springbootwebgreqit.model.Book;
import mate.academy.springbootwebgreqit.model.CartItem;
import mate.academy.springbootwebgreqit.model.Category;
import mate.academy.springbootwebgreqit.model.ShoppingCart;
import mate.academy.springbootwebgreqit.model.User;
import mate.academy.springbootwebgreqit.repository.BookRepository;
import mate.academy.springbootwebgreqit.repository.CartItemRepository;
import mate.academy.springbootwebgreqit.repository.ShoppingCartRepository;
import mate.academy.springbootwebgreqit.repository.UserRepository;
import mate.academy.springbootwebgreqit.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private User user;
    private ShoppingCart shoppingCart;
    private ShoppingCartDto shoppingCartDto;
    private CartItemRequestDto cartItemRequestDto;
    private Book book;
    private CartItem cartItem;
    private Authentication authentication;
    private UpdateCartItemDto requestUpdateQuantityDto;
    private Category category;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);

        shoppingCartDto = new ShoppingCartDto();


        cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setBookId(1L);
        cartItemRequestDto.setQuantity(2);
        cartItemRequestDto.setShoppingCartId(shoppingCart.getId());

        book = new Book();
        book.setId(1L);
        book.setTitle("wallet");
        book.setAuthor("John");
        book.setIsbn("9283234577892");
        book.setPrice(BigDecimal.valueOf(60.99));
        book.setDescription("Updated description");
        book.setCoverImage("https://example.com/updated-cover-.jpg");
        book.setCategories(Collections.singleton(category));

        category = new Category();
        category.setId(1L);
        category.setName("Roman");

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);

        authentication = mock(Authentication.class);

        requestUpdateQuantityDto = new UpdateCartItemDto();
        requestUpdateQuantityDto.setQuantity(3);
    }

    @Test
    void getShoppingCartForCurrentUser_ShouldReturnShoppingCartDto() {
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findById(shoppingCart.getId())).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.getShoppingCartForCurrentUser(authentication, shoppingCart.getId());

        assertNotNull(result);
        verify(shoppingCartRepository).findById(shoppingCart.getId());
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    void getShoppingCartForCurrentUser_ShouldThrowEntityNotFoundException_WhenShoppingCartNotFound() {
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findById(shoppingCart.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCartForCurrentUser(authentication, shoppingCart.getId()));
    }

    @Test
    void updateCartItemQuantity_ShouldUpdateQuantityAndReturnShoppingCartDto() {
        int newQuantity = requestUpdateQuantityDto.getQuantity();
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);
        when(shoppingCartRepository.findByCartItems(Set.of(cartItem))).thenReturn(Optional.of(shoppingCart));

        ShoppingCartDto result = shoppingCartService.updateCartItemQuantity(cartItem.getId(), requestUpdateQuantityDto);

        assertNotNull(result);
        assertEquals(newQuantity, cartItem.getQuantity(), "Quantity should be updated in the cart item");
        verify(cartItemRepository).save(cartItem);
        verify(shoppingCartRepository).save(shoppingCart);
        verify(shoppingCartMapper).toDto(shoppingCart);
        verifyNoMoreInteractions(cartItemRepository, shoppingCartRepository, shoppingCartMapper);
    }


    @Test
    void removeCartItem_ShouldRemoveItemAndReturnShoppingCartDto() {
        shoppingCart.setCartItems(new HashSet<>(Set.of(cartItem)));

        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.removeCartItem(cartItem.getId(), authentication);

        assertNotNull(result);
        assertFalse(shoppingCart.getCartItems().contains(cartItem), "Cart item should be removed from the cart");
        verify(shoppingCartRepository).findByUserId(user.getId());
        verify(shoppingCartRepository).save(shoppingCart);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }


    @Test
    void createShoppingCart_ShouldCreateAndReturnShoppingCart() {
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        ShoppingCart result = shoppingCartService.createShoppingCart(user);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        verify(shoppingCartRepository).save(result);
    }
}
