package com.tony.meeting.account;

        import com.tony.meeting.domain.Account;
        import lombok.Getter;
        import org.springframework.security.core.authority.SimpleGrantedAuthority;
        import org.springframework.security.core.userdetails.User;

        import java.util.List;

@Getter
public class UserAccount extends User {
    private Account account;

    //    public UserAccount(String username, String password, Collection<? extends GrantedAuthority> authorities) {
//        super(username, password, authorities);
//    }
    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }

}
