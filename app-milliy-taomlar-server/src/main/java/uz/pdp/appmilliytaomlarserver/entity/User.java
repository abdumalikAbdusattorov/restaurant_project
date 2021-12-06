package uz.pdp.appmilliytaomlarserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.pdp.appmilliytaomlarserver.entity.template.AbsEntity;
import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Users")
public class User extends AbsEntity implements UserDetails {

    @Column(unique = true)
    private String phoneNumber; //

    private String password; //

    private Integer telegramChatId;//
    private String address;//
    private String ozimAytaman;//
    private String tezroq;//
    private String botState;//
    private Double lan;//
    private Double lat;//
    private String lang;//
    private String tempOrderType;//
    private UUID tempProductId;//
    private UUID tempProductSizeId;//
    private Integer tempAmount;//
    private Integer tempMaxAmount;
    private Integer tempCategoryId;//
    private Integer page=0;//
    private Integer size=5;//
    private Date tempChusenDate; //
    private Date tempChusenTime; //

    @Column()
    private String first_name;

    @Column()
    private String last_name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roles;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;


    public User(String phoneNumber, String password, String firstName, String lastName, List<Role> roles) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.first_name = firstName;
        this.last_name = lastName;
        this.roles = roles;
    }

    @Override
    public String getUsername() {
        return this.phoneNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
