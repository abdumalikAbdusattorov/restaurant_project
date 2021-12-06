package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class ReqProduct {
    private UUID id;
    private UUID photoId;
    private String nameUz;
    private String nameRu;
    private String descriptionUz;
    private String descriptionRu;
    private double price;
    private Integer categoryId;
}
