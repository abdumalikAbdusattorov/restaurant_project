package uz.pdp.appmilliytaomlarserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.appmilliytaomlarserver.entity.template.AbsEntity;


import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DeletingMessage extends AbsEntity {
    private Integer messageId;

    private Long chatId;
}
