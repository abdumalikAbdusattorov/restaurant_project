package uz.pdp.appmilliytaomlarserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appmilliytaomlarserver.entity.Orders;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByTelegramChatId(Integer telegramChatId);
    void deleteById(UUID uuid);

    Optional<User> findByPhoneNumberContaining(String phoneNumber);
}
