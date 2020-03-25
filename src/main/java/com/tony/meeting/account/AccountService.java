package com.tony.meeting.account;

import com.tony.meeting.domain.Account;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {
    public SignUpForm register(SignUpForm signUpForm) {
        Account account = new Account();
        BeanUtils.copyProperties(signUpForm, account);
        String encryptPWD = BCrypt.hashpw(signUpForm.getPassword(), BCrypt.gensalt());
        account.setAccountId(UUID.randomUUID());
        account.setPassword(encryptPWD);
        //TODO: DB
        signUpForm.setAccountId(account.getAccountId());
        return signUpForm;
    }

    public Account save(Account account) {
        String encryptPWD = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
        account.setAccountId(UUID.randomUUID());
        account.setPassword(encryptPWD);
        return account;
    }
}
