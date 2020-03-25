package com.tony.meeting.account;

import com.tony.meeting.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("Test for checking of validated email with wrong input")
    @Test
    void checkEmailTokenWithWrongInput() throws Exception {
        mockMvc.perform(get("/account/check-email-token")
                .param("token", "abcdadsfewf2342341")
                .param("email", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"));
    }

    @DisplayName("Test for checking of validated email")
    @Test
    void checkEmailToken() throws Exception {
        Account account = Account.builder()
                .email("test@test.com")
                .password("test")
                .nickname("test")
                .build();
        Account savedAccount = accountRepository.save(account);
        savedAccount.generateEmailCheckToken();

        mockMvc.perform(get("/account/check-email-token")
                .param("token", savedAccount.getEmailCheckToken())
                .param("email", savedAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"));
    }

    @DisplayName("Test for register page")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/account/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("Test for input validation")
    @Test
    void signUpSubmitWithWrongInput() throws Exception {
        mockMvc.perform(post("/account/sign-up")
                .param("nickname", "test")
                .param("email", "test...")
                .param("password", "12")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("Test for input validation")
    @Test
    void signUpSubmitWithNormalInput() throws Exception {
        mockMvc.perform(post("/account/sign-up")
                .param("nickname", "test")
                .param("email", "test@test.com")
                .param("password", "test")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("test@test.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "test");
        assertNotNull(account.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class)); // check email
    }
}