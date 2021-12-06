package uz.pdp.appmilliytaomlarserver.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderType;
import uz.pdp.appmilliytaomlarserver.entity.enums.PayStatus;
import uz.pdp.appmilliytaomlarserver.entity.template.AbsEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Orders extends AbsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderSerialNumber;


    private Timestamp orderDateTime;

    private Float lan;
    private Float lat;
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private PayType payType;

    private double orderSum; // Zakazni summasi

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orders", cascade = CascadeType.ALL)
    private List<ProductWithAmount> productWithAmountList;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    private User operator;

    @ManyToOne(fetch = FetchType.LAZY)
    private Rooms rooms;

    private boolean fromBot;

}