package uz.pdp.appmilliytaomlarserver.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appmilliytaomlarserver.entity.DeletingMessageWithPhoto;

import java.util.List;
import java.util.UUID;

public interface DeletingMessageWithPhotoRepository extends JpaRepository<DeletingMessageWithPhoto, UUID> {
    List<DeletingMessageWithPhoto> findAllByChatId(Long chatId);
}
