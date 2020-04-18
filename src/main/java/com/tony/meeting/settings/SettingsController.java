package com.tony.meeting.settings;

import com.tony.meeting.account.AccountService;
import com.tony.meeting.account.CurrentUser;
import com.tony.meeting.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping(SettingsController.SETTINGS)
public class SettingsController {
    static final String SETTINGS = "/settings";

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String PROFILE_URL = "/profile";

    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String PASSWORD_URL = "/password";

    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String NOTIFICATIONS_URL = "/notifications";

    private final AccountService accountService;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @GetMapping("/profile")
    public String updateProfileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors, Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "Updated profile successfully!");
        return "redirect:" + SETTINGS + PROFILE_URL;
    }

    @GetMapping(PASSWORD_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "Update password successfully.");
        return "redirect:" + SETTINGS + PASSWORD_URL;
    }

    @GetMapping(NOTIFICATIONS_URL)
    public String updateNotificationsForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Notifications(account));
        return SETTINGS + NOTIFICATIONS_URL;
    }

    @PostMapping(NOTIFICATIONS_URL)
    public String updateNotifications(@CurrentUser Account account, @Valid Notifications notifications, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + NOTIFICATIONS_URL;
        }

        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "Updated notification's setting.");
        return "redirect:" + SETTINGS + NOTIFICATIONS_URL;
    }
}
