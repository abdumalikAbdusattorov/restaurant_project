package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;
import uz.pdp.appmilliytaomlarserver.entity.Attachment;

import java.util.UUID;

@Data
public class ReqRoom {
    private Integer id;

    private String number;

    private String descriptionUz;
    private String descriptionRu;

    private double price;

    private UUID attachment;
}
