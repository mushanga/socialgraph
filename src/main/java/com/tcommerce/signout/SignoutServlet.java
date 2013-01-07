package com.tcommerce.signout;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.amazonbird.config.ConfigMgr;
import com.amazonbird.config.ConfigMgrImpl;
import com.amazonbird.db.data.Announcer;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

@SuppressWarnings("serial")
public class SignoutServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(SignoutServlet.class);
	ConfigMgr configMgr = ConfigMgrImpl.getInstance();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getSession().removeAttribute(Announcer.ANNOUNCER);
		response.sendRedirect(request.getContextPath() + "/");
	}
}
