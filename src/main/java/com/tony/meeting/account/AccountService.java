package com.tony.meeting.account;

import com.tony.meeting.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

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

    public void sendSignUpConfirmedEmail(Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("[Meeting] verification of registration");
        mailMessage.setText("/account/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmedEmail(newAccount);
        return newAccount;
    }


    public Account save(Account account) {
        String encryptPWD = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
        account.setAccountId(UUID.randomUUID());
        account.setPassword(encryptPWD);
        return account;
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                account.getNickname(),
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if(account == null) {
            throw new UsernameNotFoundException("Wrong account : " + emailOrNickname);
        }
        return new UserAccount(account);
    }
}
