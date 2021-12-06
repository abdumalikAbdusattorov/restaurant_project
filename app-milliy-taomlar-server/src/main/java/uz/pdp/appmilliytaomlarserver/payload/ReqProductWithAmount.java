package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;

import java.util.UUID;
@Data
public class ReqProductWithAmount {
    private UUID id;
    private UUID productId;
    private Integer amount;
    private Integer canceledAmount;
}
