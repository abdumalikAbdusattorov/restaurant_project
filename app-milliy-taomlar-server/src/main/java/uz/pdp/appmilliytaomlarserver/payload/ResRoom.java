package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class ResRoom {
    private Integer id;

    private String number;

    private String descriptionUz;
    private String descriptionRu;

    private double price;

    private UUID attachment;
}
