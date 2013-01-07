package com.tcommerce.signin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.amazonbird.config.ConfigMgr;
import com.amazonbird.config.ConfigMgrImpl;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

@SuppressWarnings("serial")
public class SigninServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(SigninServlet.class);
	ConfigMgr configMgr = ConfigMgrImpl.getInstance();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		

		Twitter twitter = new TwitterFactory().getInstance();
		try {
			StringBuffer callbackURL = request.getRequestURL();
			int index = callbackURL.lastIndexOf("/");
			callbackURL.replace(index, callbackURL.length(), "").append(
					"/tcallback");
			logger.debug("Callback url is: " + callbackURL.toString());
			twitter.setOAuthConsumer(configMgr.getConsumerKey(),
					configMgr.getConsumerSecret());
			logger.debug("Consumer Key: " + configMgr.getConsumerKey()
					+ ", Consumer Secret: " + configMgr.getConsumerSecret());
			RequestToken requestToken = twitter
					.getOAuthRequestToken(callbackURL.toString());
			request.getSession().setAttribute(CallBackServlet.REQUEST_TOKEN, requestToken);
			response.sendRedirect(requestToken.getAuthenticationURL());
			logger.debug("Redirect sent to authentication URL: "
					+ requestToken.getAuthenticationURL());

		} catch (TwitterException e) {
			throw new ServletException(e);
		}
	}
}
