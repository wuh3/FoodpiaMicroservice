package com.foodopia.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestUserDto {

    @NotBlank(message = "Username is required")
    @Size(min = 6, message = "Username must be at least 6 characters long")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Username must contain only letters and numbers")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    // Must contain at least one lower case letter, one upper case letter, and one digit.
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain lower case, upper case letters and a digit")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

}
