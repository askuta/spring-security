package com.zoroga.guestapp.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LandonUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthGroupRepository authGroupRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (null == user) {
			throw new UsernameNotFoundException("Cannot find username: " + username);
		}

        List<AuthGroup> authGroups = this.authGroupRepository.findByUsername(username);

        return new LandonUserPrincipal(user, authGroups);
	}
}
