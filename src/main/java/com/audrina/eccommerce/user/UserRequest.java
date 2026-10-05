package com.audrina.eccommerce.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Name must be provided")
    private  String name;
    @NotBlank(message = "Email must be provided")
    @Email(message = "email must be uniquely specified")
    private  String email;
    @NotBlank(message = "Password must be provided")
    private  String password;
}
