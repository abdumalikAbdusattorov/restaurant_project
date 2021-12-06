package uz.pdp.appmilliytaomlarserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appmilliytaomlarserver.entity.Attachment;
import uz.pdp.appmilliytaomlarserver.entity.AttachmentContent;

import java.util.Optional;
import java.util.UUID;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, UUID> {

AttachmentContent getByAttachment(Attachment attachment);
    Optional<AttachmentContent> findByAttachment(Attachment attachment);
}
