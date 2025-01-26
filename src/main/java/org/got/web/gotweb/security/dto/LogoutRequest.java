package org.got.web.gotweb.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.got.web.gotweb.common.annotations.ToLowerCase;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    //TODO : Soit username soit email doit être obligatoire
    //@NotBlank(message = "Username cannot be blank")
    @ToLowerCase
    private String username;

    @ToLowerCase
    private String email; //todo: to implement

}
