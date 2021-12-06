package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;

@Data
public class ReqPayType {
    private Integer id;
    private String nameUz;
    private String nameRu;
    private boolean active;
}
