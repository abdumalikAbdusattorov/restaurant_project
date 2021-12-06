package uz.pdp.appmilliytaomlarserver.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class ReqSignUp {
    private UUID id;

    private String phoneNumber;

    private String password;

    private String firstName;

    private String lastName;

}
