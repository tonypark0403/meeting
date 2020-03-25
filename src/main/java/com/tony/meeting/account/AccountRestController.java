package com.tony.meeting.account;

import com.tony.meeting.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountRestController {
    @Autowired
    AccountService accountService;

    @PostMapping
    ResponseEntity<Account> save(@RequestBody Account account) {
        return new ResponseEntity<Account>(accountService.save(account), HttpStatus.CREATED);
    }
}
