package uz.pdp.appmilliytaomlarserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponse;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponseModel;
import uz.pdp.appmilliytaomlarserver.payload.ReqSignUp;
import uz.pdp.appmilliytaomlarserver.repository.UserRepository;
import uz.pdp.appmilliytaomlarserver.security.AuthService;
import uz.pdp.appmilliytaomlarserver.security.CurrentUser;
import uz.pdp.appmilliytaomlarserver.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthService authService;


    @GetMapping("/me")
    public HttpEntity<?> getUser(@CurrentUser User user) {
        return ResponseEntity.ok(new ApiResponseModel(user!=null?true:false, user!=null?"Mana user":"Error", user));
    }

    @PreAuthorize("hasAnyRole('ROLE_COORDINATOR','ROLE_ADMIN','ROLE_DIRECTOR')")
    @PostMapping
    public HttpEntity<?> createUser(@RequestBody ReqSignUp reqUser) {
        ApiResponse response = userService.addUser(reqUser);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(response.getMessage(), true));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(response.getMessage(), false));
    }



    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_DIRECTOR')")
    @GetMapping
    public HttpEntity<?> getUsers() {
        return ResponseEntity.ok(new ApiResponseModel(true, "Mana userlar", userService.getUsers()));
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_DIRECTOR')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteUser(@PathVariable UUID id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse("Deleted", true));
    }



//    @PutMapping("/edit")
//    public HttpEntity<?> editUser(@RequestBody ReqUser reqUser, @CurrentUser User user) {
//        return userService.editUser(reqUser, user);
//    }


}
