package com.debatree.main;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.db.data.Announcer;

public class AuthFilter implements Filter {

	public static final String SESS_USER = "session_user";

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(OAuth.COOKIE_NAME)) {
					Announcer ann = AnnouncerMgrImpl.getInstance().getAnnouncerByAccessToken(cookie.getValue());
					request.setAttribute(SESS_USER, ann);
				}
			}

		}
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}