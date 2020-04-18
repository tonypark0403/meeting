package com.tony.meeting.settings;

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
        //modelMapper(Bean으로 등록된...)를 사용할 경우는 이게 빈이 아니라서 사용할 수 없음
        BeanUtils.copyProperties(account, this);
    }
}
