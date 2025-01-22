package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailService(UserRepository userRepository){
       this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = userRepository.findByEmailAddress(username);
        if(userOptional.isPresent()){
            return userOptional.orElseThrow();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
