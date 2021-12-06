package uz.pdp.appmilliytaomlarserver.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.appmilliytaomlarserver.entity.AttachmentType;

import java.util.UUID;

@Projection(name = "customAttachmentType", types = AttachmentType.class)
public interface CustomAttachmentType {
    UUID getId();
    String getContentTypes();
    Integer getWidth();
    Integer getHeight();
    String getType();
}
