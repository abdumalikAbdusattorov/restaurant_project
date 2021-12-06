package uz.pdp.appmilliytaomlarserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appmilliytaomlarserver.entity.Orders;
import uz.pdp.appmilliytaomlarserver.entity.Rooms;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;

import java.sql.Timestamp;
import java.util.Optional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdersRepository extends JpaRepository<Orders, UUID> {

    Optional<Orders> findByOrderStatusAndClient(OrderStatus orderStatus, User client);
    Optional<Orders> findByOrderDateTimeAndRooms(Timestamp orderDateTime, Rooms rooms);


    Page<Orders> findAllByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    Page<Orders> findAllByCreatedAtBetween(Timestamp startDate, Timestamp endDate, Pageable pageable);


}
