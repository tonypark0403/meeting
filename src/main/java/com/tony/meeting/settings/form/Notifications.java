package com.tony.meeting.settings.form;

import com.tony.meeting.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
public class Notifications {

    private boolean meetingCreatedByEmail;

    private boolean meetingCreatedByWeb;

    private boolean meetingEnrollmentResultByEmail;

    private boolean meetingEnrollmentResultByWeb;

    private boolean meetingUpdatedByEmail;

    private boolean meetingUpdatedByWeb;


    public Notifications(Account account) {
        BeanUtils.copyProperties(account, this);
    }
}
