package com.tony.meeting.account;

import com.tony.meeting.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .meetingCreatedByWeb(true)
                .meetingEnrollmentResultByWeb(true)
                .meetingUpdatedByWeb(true)
                .build();
//        String encryptPWD = BCrypt.hashpw(signUpForm.getPassword(), BCrypt.gensalt());
//        account.setPassword(encryptPWD);
        return accountRepository.save(account);
    }

    private void sendSignUpConfirmedEmail(Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("[Meeting] verification of registration");
        mailMessage.setText("/account/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }

    @Transactional
    public SignUpForm processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmedEmail(newAccount);
        BeanUtils.copyProperties(newAccount, signUpForm);
        return signUpForm;
    }


    public Account save(Account account) {
        String encryptPWD = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
        account.setAccountId(UUID.randomUUID());
        account.setPassword(encryptPWD);
        return account;
    }
}
