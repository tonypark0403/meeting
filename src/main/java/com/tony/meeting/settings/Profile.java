package com.tony.meeting.settings;

import com.tony.meeting.domain.Account;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class Profile {
    private String bio;
    private String url;
    private String occupation;
    private String location;
    public Profile(Account account) {
        BeanUtils.copyProperties(account, this);
    }
}
