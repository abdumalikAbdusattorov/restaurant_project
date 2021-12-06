package uz.pdp.appmilliytaomlarserver.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.appmilliytaomlarserver.entity.template.AbsEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductWithAmount extends AbsEntity {
    private int amount;
    private int cancelledAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;



    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Orders orders;

}
