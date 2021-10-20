package com.mycompany.webapp.security;

import java.util.Collection;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.mycompany.webapp.service.CustomUserDetailsService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CustomUserDetails extends User{

	private String useremail;
	
	public CustomUserDetails(
			String username,
			String password,
			boolean enabled,
			Collection<? extends GrantedAuthority> authorities,
			String useremail) {
		
		super(username, password, enabled, true, true, true, authorities);
		this.useremail = useremail;
	}

	public String getUseremail() {
		return useremail;
		
	}
	
}
