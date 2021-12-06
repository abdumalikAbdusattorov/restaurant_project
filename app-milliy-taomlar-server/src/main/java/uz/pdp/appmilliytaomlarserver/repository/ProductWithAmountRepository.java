package uz.pdp.appmilliytaomlarserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appmilliytaomlarserver.entity.Orders;
import uz.pdp.appmilliytaomlarserver.entity.ProductWithAmount;

import java.util.List;
import java.util.UUID;

public interface ProductWithAmountRepository extends JpaRepository<ProductWithAmount, UUID> {
    List<ProductWithAmount> findAllByOrders(Orders orders);
}
