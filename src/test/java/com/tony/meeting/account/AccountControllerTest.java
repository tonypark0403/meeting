package com.tony.meeting.account;

import com.tony.meeting.ConsoleMailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    ConsoleMailSender javaMailSender;

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

        assertTrue(accountRepository.existsByEmail("test@test.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class)); // check email
    }
}