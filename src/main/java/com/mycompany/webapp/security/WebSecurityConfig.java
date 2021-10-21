package com.mycompany.webapp.security;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import com.mycompany.webapp.service.CustomUserDetailsService;

import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Resource
	private DataSource dataSource;
	
	@Resource
	private CustomUserDetailsService customUserDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("configure(HttpSecurity http) 실행");
		http.formLogin() //폼을 통해서 아이디와 비밀번호를 입력해서 인증을 하겠다.
			.loginPage("/security/loginForm")		//default : /login(GET)
			.usernameParameter("mlogin_id")				//default : username
			.passwordParameter("mpw") 		//default : password
			.loginProcessingUrl("/login") 			//default : /login(POST)
			.defaultSuccessUrl("/")	
			.failureUrl("/security/loginError");	//default : /login?error
		
		/*로그아웃 설정*/
		http.logout()
			.logoutUrl("/logout")					//default : /logout
			.logoutSuccessUrl("/");
		
		/*url 권한 설정*/
		http.authorizeRequests()
			.antMatchers("/security/admin/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/security/manager/**").hasAuthority("ROLE_MANAGER")
			.antMatchers("/security/user/**", "/order/orderList/**", "/cart/2").authenticated()
			.antMatchers("/**").permitAll();
		
		/*권한 없음(403에러)일 경우 이동할 경로 설정*/
		http.exceptionHandling().accessDeniedPage("/security/accessDenied");
		
		/*csrf 비활성화*/
		http.csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		log.info("configure(AuthenticationManagerBuilder auth) 실행");
		//데이터베이스에서 가져올 사용자 정보 조회 설정
		//패스워드 인코딩 방법 설정
		/*
		auth.jdbcAuthentication()
			.dataSource(dataSource)		
			.usersByUsernameQuery("SELECT mid, mpassword, menabled FROM member WHERE mid=?")
			.authoritiesByUsernameQuery("SELECT mid, mrole FROM member WHERE mid=?")
			.passwordEncoder(passwordEncoder());	//default : DelegatingPasswordEncoder
		*/
		
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		auth.authenticationProvider(provider);
	}
	//@Bean -> 메서드가 리턴하는 객체(현재는 PasswordEncoder)를 관리객체로 만들어주는 역할 
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		
		// 이렇게 되면 db의 password엔 {암호화종류}가 없어야 하고 bcrypt로 암호화한 값만 있어야 함
//		PasswordEncoder pe = new BCryptPasswordEncoder();
		return pe;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		log.info("configure(WebSecurity web) 실행");
		
		//권한 계층 설정
		DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
		handler.setRoleHierarchy(roleHierarchyImpl());
		web.expressionHandler(handler);
		web.ignoring()
			.antMatchers("/static/**")
			.antMatchers("/images/**")
			.antMatchers("/bootstrap-4.6.0-dist/**")
			.antMatchers("/css/**")
			.antMatchers("/jquery/**")
			.antMatchers("/favicon.ico");
	}
	
	//권한 계층을 참조하기 위해 HttpSecurity에서 사용하기 때문에 관리빈으로 반드시 등록해야 함.
	@Bean
	public RoleHierarchyImpl roleHierarchyImpl() {
		RoleHierarchyImpl roleHierarchyImpl = new RoleHierarchyImpl();
		roleHierarchyImpl.setHierarchy("ROLE_ADMIN > ROLE_MANAGER > ROLE_USER");
		return roleHierarchyImpl;
	}
	
}
