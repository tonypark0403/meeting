package com.tony.meeting.settings;

import com.tony.meeting.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
public class Profile {
    @Length(max = 35)
    private String bio;
    @Length(max = 50)
    private String url;
    @Length(max = 50)
    private String occupation;
    @Length(max = 50)
    private String location;
    private String profileImage;
    public Profile(Account account) {
        BeanUtils.copyProperties(account, this);
    }
}
