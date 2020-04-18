package com.tony.meeting.settings.form;

import com.tony.meeting.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class NicknameForm {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z_]{3,20}$")
    private String nickname;

    public NicknameForm(Account account) {
        BeanUtils.copyProperties(account, this);
    }
}
