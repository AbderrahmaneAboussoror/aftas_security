package com.example.competitionmanagment.Config;

import com.example.competitionmanagment.entity.User;
import com.example.competitionmanagment.entity.UserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor

public class UserInfoConfig implements UserDetails {

    private final User user;
    private int userid;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {


        return Arrays.stream(user.getRole().toString()
                        .split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public int getUserid(){
        return this.userid = user.getNum();
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
