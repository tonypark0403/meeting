package com.tony.meeting.settings;

import com.tony.meeting.WithAccount;
import com.tony.meeting.account.AccountRepository;
import com.tony.meeting.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("test")
    @DisplayName("Update profile form")
    @Test
    void updateProfileForm() throws Exception {
        String bio = "case of updating bio";
        mockMvc.perform(get(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("test")
    @DisplayName("Update profile - normal input")
    @Test
    void updateProfile() throws Exception {
        String bio = "case of updating bio";
        mockMvc.perform(post(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertEquals(bio, test.getBio());
    }

    @WithAccount("test")
    @DisplayName("Update profile - too long input")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "case of updating bio more than 35 ...................................................................................................";
        mockMvc.perform(post(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS + SettingsController.PROFILE_URL))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account test = accountRepository.findByNickname("test");
        assertNull(test.getBio());
    }

    @WithAccount("test")
    @DisplayName("Update password form")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("Update password - normal input")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PASSWORD_URL)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(passwordEncoder.matches("12345678", test.getPassword()));
    }

    @WithAccount("test")
    @DisplayName("Update password - wrong input")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PASSWORD_URL)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name(SettingsController.SETTINGS + SettingsController.PASSWORD_URL));
    }

    @WithAccount("test")
    @DisplayName("Update password - short input")
    @Test
    void updatePassword_shortInput() throws Exception {
        mockMvc.perform(post(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.PASSWORD_URL)
                .param("newPassword", "1234")
                .param("newPasswordConfirm", "1234")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name(SettingsController.SETTINGS + SettingsController.PASSWORD_URL));;
    }

    @WithAccount("test")
    @DisplayName("Update notifications form")
    @Test
    void updateNotifications_form() throws Exception {
        mockMvc.perform(get(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.NOTIFICATIONS_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    @WithAccount("test")
    @DisplayName("Update notifications - normal input")
    @Test
    void updateNotifications_success() throws Exception {
        mockMvc.perform(post(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.NOTIFICATIONS_URL)
                .param("meetingCreatedByEmail", "true")
                .param("meetingCreatedByWeb", "true")
                .param("meetingEnrollmentResultByEmail", "true")
                .param("meetingEnrollmentResultByWeb", "true")
                .param("meetingUpdatedByEmail", "true")
                .param("meetingUpdatedByWeb", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.NOTIFICATIONS_URL))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(test.isMeetingCreatedByEmail());
        assertTrue(test.isMeetingCreatedByWeb());
        assertTrue(test.isMeetingEnrollmentResultByEmail());
        assertTrue(test.isMeetingEnrollmentResultByWeb());
        assertTrue(test.isMeetingUpdatedByEmail());
        assertTrue(test.isMeetingUpdatedByWeb());
    }

    @WithAccount("test")
    @DisplayName("Update nickname form")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("test")
    @DisplayName("Update nickname - normal input")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "test123";
        mockMvc.perform(post(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.ACCOUNT_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname("test123"));
    }

    @WithAccount("test")
    @DisplayName("Update nickname - wrong input")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "¯\\-test-";
        mockMvc.perform(post(SettingsController.ROOT + SettingsController.SETTINGS + SettingsController.ACCOUNT_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS + SettingsController.ACCOUNT_URL))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }
}