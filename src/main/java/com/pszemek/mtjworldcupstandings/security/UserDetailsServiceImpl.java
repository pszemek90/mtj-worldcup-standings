package com.pszemek.mtjworldcupstandings.security;

import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.mapper.UserDtoEntityMapper;
import com.pszemek.mtjworldcupstandings.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));

        return UserDtoEntityMapper.mapToDto(user);
    }
}
