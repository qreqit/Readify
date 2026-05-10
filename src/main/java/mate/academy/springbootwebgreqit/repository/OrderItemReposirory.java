package mate.academy.springbootwebgreqit.repository;

import mate.academy.springbootwebgreqit.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemReposirory extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrderId(Long orderId);

    Optional<OrderItem> findByOrderIdAndId(Long orderId, Long itemId);
}
