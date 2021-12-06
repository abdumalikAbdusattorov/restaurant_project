package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderType;
import uz.pdp.appmilliytaomlarserver.entity.enums.PayStatus;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class ReqOrder {
    private UUID id;
    private UUID clientId;
    private String clientPhoneNumber;
    private String clientFirstName;
    private String clientLastName;
    private String orderAdress;
    private Date date;
    //private Time time;
    private Float lan;
    private Float lat;
    private List<ReqProductWithAmount> reqProductWithAmountList;
    private Integer payTypeId;
    private OrderStatus orderStatus;
    private OrderType orderType;
    private PayStatus payStatus;
    private Integer roomsId;
}
