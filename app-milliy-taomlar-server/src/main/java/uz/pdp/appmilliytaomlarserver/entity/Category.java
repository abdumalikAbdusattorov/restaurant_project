package uz.pdp.appmilliytaomlarserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uz.pdp.appmilliytaomlarserver.entity.template.AbsNameEntity;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Category extends AbsNameEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    private String telegramIcon;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "parent",cascade = CascadeType.ALL)
    private List<Category> childCategories;
}
