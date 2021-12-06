package uz.pdp.appmilliytaomlarserver.security;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponse;
import uz.pdp.appmilliytaomlarserver.payload.ReqSignUp;
import uz.pdp.appmilliytaomlarserver.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;

    private final MessageSource messageSource;

    public AuthService(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException(phoneNumber));
    }


    public UserDetails loadUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User id not found: " + userId));
    }


    public ApiResponse register(ReqSignUp reqSignUp) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(reqSignUp.getPhoneNumber());
        if (optionalUser.isPresent()) {
            return new ApiResponse(messageSource.getMessage("phone.number.exist", null, LocaleContextHolder.getLocale()), false);
        } else {
//            User user = new User(reqSignUp.getPhoneNumber(),
//                    passwordEncoder.encode(reqSignUp.getPassword()),
//                    reqSignUp.getFirstName(),
//                    reqSignUp.getLastName(),
//                    roleRepository.findAllById(reqSignUp.getRolesId()));
//            userRepository.save(user);
            return new ApiResponse(messageSource.getMessage("user.created", null, LocaleContextHolder.getLocale()), true);
        }
    }


}
