package uz.pdp.appmilliytaomlarserver.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.entity.enums.RoleName;
import uz.pdp.appmilliytaomlarserver.repository.RoleRepository;
import uz.pdp.appmilliytaomlarserver.repository.UserRepository;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Value("${spring.datasource.initialization-mode}")
    private String initialMode;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")) {
            userRepository.save(new User(

                    "4444",

                    passwordEncoder.encode("4444"),
                    "Boburjon",
                    "Obidov",
                    roleRepository.findAllByNameIn(
                            Arrays.asList(RoleName.ROLE_ADMIN,
                                    RoleName.ROLE_DIRECTOR,RoleName.ROLE_MANAGER)
                    )));
        }

    }
}
