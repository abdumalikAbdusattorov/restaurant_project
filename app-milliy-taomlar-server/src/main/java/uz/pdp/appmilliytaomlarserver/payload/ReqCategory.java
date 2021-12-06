package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;

@Data
public class ReqCategory {
    private Integer id;
    private String nameUz;
    private String nameRu;
    private String telegramIcon;
    private Integer parentId;
}
