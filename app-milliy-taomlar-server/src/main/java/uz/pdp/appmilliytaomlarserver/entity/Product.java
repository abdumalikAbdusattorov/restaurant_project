package uz.pdp.appmilliytaomlarserver.entity;

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
public class Product extends AbsEntity {
    private String nameUz;

    private String nameRu;

    private String descriptionUz;

    private String descriptionRu;

    private boolean active;

    private double price;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Attachment attachment;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Category category;

}
