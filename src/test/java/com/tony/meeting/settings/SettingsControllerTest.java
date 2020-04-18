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
        mockMvc.perform(get(SettingsController.SETTINGS + SettingsController.PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("test")
    @DisplayName("Update profile - normal input")
    @Test
    void updateProfile() throws Exception {
        String bio = "case of updating bio";
        mockMvc.perform(post(SettingsController.SETTINGS + SettingsController.PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS + SettingsController.PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertEquals(bio, test.getBio());
    }

    @WithAccount("test")
    @DisplayName("Update profile - too long input")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "case of updating bio more than 35 ...................................................................................................";
        mockMvc.perform(post(SettingsController.SETTINGS + SettingsController.PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
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
        mockMvc.perform(get(SettingsController.SETTINGS + SettingsController.PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("Update password - normal input")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS + SettingsController.PASSWORD_URL)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS + SettingsController.PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(passwordEncoder.matches("12345678", test.getPassword()));
    }

    @WithAccount("test")
    @DisplayName("Update password - wrong input")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS + SettingsController.PASSWORD_URL)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("test")
    @DisplayName("Update password - short input")
    @Test
    void updatePassword_shortInput() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS + SettingsController.PASSWORD_URL)
                .param("newPassword", "1234")
                .param("newPasswordConfirm", "1234")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("test")
    @DisplayName("Update notifications form")
    @Test
    void updateNotifications_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS + SettingsController.NOTIFICATIONS_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    @WithAccount("test")
    @DisplayName("Update notifications - normal input")
    @Test
    void updateNotifications_success() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS + SettingsController.NOTIFICATIONS_URL)
                .param("meetingCreatedByEmail", "true")
                .param("meetingCreatedByWeb", "true")
                .param("meetingEnrollmentResultByEmail", "true")
                .param("meetingEnrollmentResultByWeb", "true")
                .param("meetingUpdatedByEmail", "true")
                .param("meetingUpdatedByWeb", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS + SettingsController.NOTIFICATIONS_URL))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(test.isMeetingCreatedByEmail());
        assertTrue(test.isMeetingCreatedByWeb());
        assertTrue(test.isMeetingEnrollmentResultByEmail());
        assertTrue(test.isMeetingEnrollmentResultByWeb());
        assertTrue(test.isMeetingUpdatedByEmail());
        assertTrue(test.isMeetingUpdatedByWeb());
    }

}