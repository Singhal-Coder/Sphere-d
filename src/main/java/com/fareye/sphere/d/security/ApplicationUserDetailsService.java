package com.fareye.sphere.d.security;

import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final IdUtils idUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> UserPrincipal.from(
                        user,
                        idUtils.formatUserId(user.getUserId())
                                .orElseThrow(() -> new UsernameNotFoundException("Invalid user id"))))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
