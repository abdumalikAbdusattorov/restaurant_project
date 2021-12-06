package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;
import uz.pdp.appmilliytaomlarserver.entity.Orders;

import java.util.UUID;

@Data
public class ResProductWithAmount {
    private UUID id;
    private int amount;
    private ResProduct resProduct;
    private Orders orders;
}
