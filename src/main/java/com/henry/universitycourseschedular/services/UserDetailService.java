package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.repositories.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    public UserDetailService(AppUserRepository appUserRepository){
       this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = appUserRepository.findByEmailAddress(username);
        if(userOptional.isPresent()){
            return userOptional.orElseThrow();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
