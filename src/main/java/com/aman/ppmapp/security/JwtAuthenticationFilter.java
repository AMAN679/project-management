package com.aman.ppmapp.security;

import java.io.IOException;

import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aman.ppmapp.domain.User;
import com.aman.ppmapp.services.CustomUserDetailsService;


public class JwtAuthenticationFilter extends OncePerRequestFilter{

	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	try {

		String jwt=getJWTFromRequest(request);
		if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt))
		{
			Long userId=tokenProvider.getUserIdFromJWT(jwt);
			User userDetails=customUserDetailsService.loadUserById(userId);
			//UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null, (Collection<? extends GrantedAuthority>) Collections.emptyList());
		    
			UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null,Collections.<GrantedAuthority>emptyList());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		    SecurityContextHolder.getContext().setAuthentication(authentication);
		
		}
	}
	catch(Exception ex)
	{
		
		logger.error("Could not set user authentication in security context",ex);
	}
	
	filterChain.doFilter(request, response);
	
	}
	
	
	private String getJWTFromRequest(HttpServletRequest request)
	{
		String bearerToken=request.getHeader(SecurityConstants.HEADER_STRING);
		
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX))
		{
			return bearerToken.substring(7,bearerToken.length());
		}
		return null;
	}
	
	

}







