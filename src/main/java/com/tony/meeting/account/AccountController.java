package com.tony.meeting.account;

import com.tony.meeting.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping("/sign-up")
    public String signUpForm(){
        return "account/sign-up";
    }
    @PostMapping("/sign-up")
    ResponseEntity<Account> save(@RequestBody Account account) {
        return new ResponseEntity<Account>(accountService.save(account), HttpStatus.CREATED);
    }
}
