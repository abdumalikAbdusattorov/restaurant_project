package uz.pdp.appmilliytaomlarserver.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Rooms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String number;

    private String descriptionUz;
    private String descriptionRu;

    private double price;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Attachment attachment;
}
