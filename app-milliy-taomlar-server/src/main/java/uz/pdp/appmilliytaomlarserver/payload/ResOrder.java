package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;
import uz.pdp.appmilliytaomlarserver.entity.PayType;
import uz.pdp.appmilliytaomlarserver.entity.Rooms;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;
import uz.pdp.appmilliytaomlarserver.entity.enums.PayStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class ResOrder {
    private UUID id;
    private Timestamp createdAt;
    private User user;
    private String orderAddress;
    private Float lan;
    private Float lat;
    private Integer payType;
    private double orderSum;
    private List<ResProductWithAmount> resProductWithAmountList;
    private OrderStatus orderStatus;
    private PayStatus payStatus;
    private ResUser resUser;
    private Rooms rooms;
    private Timestamp orderDateAndTime;
    private boolean fromBot;
}
