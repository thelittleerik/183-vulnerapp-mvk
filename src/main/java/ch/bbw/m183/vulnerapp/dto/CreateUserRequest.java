package ch.bbw.m183.vulnerapp.dto;

import ch.bbw.m183.vulnerapp.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(min= 3, max = 100)
        String fullname,

        @NotBlank
        @Size(min = 12, max = 100)
        @Pattern(
                //https://regexbox.com/regex-templates/password
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Das Passwort muss mindestens 12 Zeichen lang sein und einen Grossbuchstaben, " +
                        "einen Kleinbuchstaben, eine Ziffer sowie ein Sonderzeichen (@$!%*?&) enthalten"
        )
        String password,

        @NotNull
        Role role

) {}
