package uz.pdp.appmilliytaomlarserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CompanyInfo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String descriptionUz;

    private String descriptionRu;

    private Integer deliveryPrice;

    private boolean botActive;

    private boolean deliveryActive;

    private boolean percentAndSom;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Attachment attachment;

}
