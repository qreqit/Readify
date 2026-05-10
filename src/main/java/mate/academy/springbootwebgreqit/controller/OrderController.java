package mate.academy.springbootwebgreqit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootwebgreqit.dto.order.CreateOrderRequestDto;
import mate.academy.springbootwebgreqit.dto.order.OrderRequestDto;
import mate.academy.springbootwebgreqit.dto.order.OrderResponseDto;
import mate.academy.springbootwebgreqit.dto.orderitem.OrderItemResponseDto;
import mate.academy.springbootwebgreqit.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Make order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDto addOrder(@RequestParam Long userId,
                                     @RequestBody CreateOrderRequestDto createOrderRequestDto) {
        return orderService.addOrder(userId, createOrderRequestDto);
    }

    @Operation(summary = "Get all orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<OrderResponseDto> getAllOrders(@RequestParam Long userId) {
        return orderService.getAllOrders(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Update order status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public OrderResponseDto updateOrderStatus(@PathVariable Long id,
                                              @RequestBody @Valid OrderRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }

    @Operation(summary = "Get all items from order by order id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order items retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    public List<OrderItemResponseDto> getAllItemsFromOrder(@PathVariable Long orderId,
                                                           Authentication authentication) {
        return orderService.getAllItemsFromOrder(orderId);
    }

    @Operation(summary = "Get item from order by order id and item id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order item retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order or item not found")
    })
    @GetMapping("{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public OrderItemResponseDto getItemFromOrderById(@PathVariable Long orderId,
                                                     @PathVariable Long itemId) {
        return orderService.getItemFromOrderById(orderId, itemId);
    }
}
