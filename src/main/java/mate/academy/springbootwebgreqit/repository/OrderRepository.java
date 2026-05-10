package mate.academy.springbootwebgreqit.repository;

import mate.academy.springbootwebgreqit.model.Order;
import mate.academy.springbootwebgreqit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
