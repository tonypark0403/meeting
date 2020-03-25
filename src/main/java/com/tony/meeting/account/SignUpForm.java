package com.tony.meeting.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Data
public class SignUpForm {
    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 4, max = 50)
    private String password;

    private UUID accountId;
}
