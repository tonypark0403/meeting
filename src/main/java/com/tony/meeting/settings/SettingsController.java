package com.tony.meeting.settings;

import com.tony.meeting.account.AccountService;
import com.tony.meeting.account.CurrentUser;
import com.tony.meeting.domain.Account;
import com.tony.meeting.settings.form.NicknameForm;
import com.tony.meeting.settings.form.Notifications;
import com.tony.meeting.settings.form.PasswordForm;
import com.tony.meeting.settings.form.Profile;
import com.tony.meeting.settings.validator.NicknameValidator;
import com.tony.meeting.settings.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping(SettingsController.ROOT + SettingsController.SETTINGS)
public class SettingsController {
    static final String ROOT = "/";
    static final String SETTINGS = "settings";
    static final String PROFILE_URL = "/profile";
    static final String PASSWORD_URL = "/password";
    static final String NOTIFICATIONS_URL = "/notifications";
    static final String ACCOUNT_URL = "/account";

    private final AccountService accountService;
    private final NicknameValidator nicknameValidator;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(PROFILE_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS + PROFILE_URL;
    }

    @PostMapping(PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors, Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + PROFILE_URL;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "Updated profile successfully!");
        return "redirect:" + ROOT + SETTINGS + PROFILE_URL;
    }

    @GetMapping(PASSWORD_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS + PASSWORD_URL;
    }

    @PostMapping(PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + PASSWORD_URL;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "Update password successfully.");
        return "redirect:" + ROOT + SETTINGS + PASSWORD_URL;
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
        return "redirect:" + ROOT + SETTINGS + NOTIFICATIONS_URL;
    }

    @GetMapping(ACCOUNT_URL)
    public String updateAccountForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new NicknameForm(account));
        return SETTINGS + ACCOUNT_URL;
    }

    @PostMapping(ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + ACCOUNT_URL;
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "Updated your nickname.");
        return "redirect:" + ROOT + SETTINGS + ACCOUNT_URL;
    }
}
