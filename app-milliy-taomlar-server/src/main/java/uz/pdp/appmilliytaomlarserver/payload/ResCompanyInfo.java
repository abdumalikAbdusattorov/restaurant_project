package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class ResCompanyInfo {

    private Integer id;

    private String name;

    private String descriptionUz;

    private String descriptionRu;

    private Integer deliveryPrice;

    private boolean botActive;

    private boolean deliveryActive;

    private boolean percentAndSom;

    private UUID attachment;
}
