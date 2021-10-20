package com.mycompany.webapp.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mycompany.webapp.dao.MemberDao;
import com.mycompany.webapp.dto.Member;
import com.mycompany.webapp.security.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
	
	@Resource
	private MemberDao memberDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("실행");
		//log.info("username " + username);
		Member member = memberDao.selectByMid(username);
		if(member == null) {
			throw new UsernameNotFoundException(username);
		}
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		//log.info("mrole " + member.getMrole());
		authorities.add(new SimpleGrantedAuthority(member.getMrole()));
		
		CustomUserDetails userDetails = new CustomUserDetails(
			member.getMlogin_id(),
			member.getMpw(),
			member.isMenabled(),
			authorities,
			member.getMemail()
		);
		
		return userDetails;
	}
	
	
}
