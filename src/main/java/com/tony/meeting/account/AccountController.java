package com.tony.meeting.account;

import com.tony.meeting.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

//        signUpFormValidator.validate(signUpForm, errors);
//        if (errors.hasErrors()) {
//            return "account/sign-up";
//        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        log.info("signUpForm after service : " + account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        final String view = "account/checked-email";
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if (!account.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }
        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/recheck-email")
    public String reCheckEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/recheck-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model) {
        if(!account.canSendConfirmEmail()) {
            model.addAttribute("error", "Please re-send after 1 hour.");
            model.addAttribute("email", account.getEmail());
            return "account/recheck-email";
        }

        accountService.sendSignUpConfirmedEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account) {
        Account byNickname = accountRepository.findByNickname(nickname);
        if(byNickname == null) {
            throw new IllegalArgumentException("No exist nickname, " + nickname);
        }

        model.addAttribute(byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));
        return "account/profile";
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", email + " is invalid.");
            return "account/email-login";
        }

        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "Email login is available after 1 hour.");
            return "account/email-login"; // for test, commented
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "An email verification email has been sent.");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";
        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "You cannot log in.");
            return view;
        }

        accountService.login(account);
        return view;
    }
}
