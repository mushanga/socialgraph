package com.amazonbird.route;

import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.Click;
import com.amazonbird.db.data.Product;
import com.amazonbird.util.Util;

public class Redirection {

	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	ClickMgrImpl clickMgr = ClickMgrImpl.getInstance();
	private ExecutorService pool = Executors.newSingleThreadExecutor();
	Util util = Util.getInstance();
	private static Logger logger = Logger.getLogger(Redirection.class);

	private static Redirection instance = new Redirection();

	public static Redirection getInstance() {
		return instance;
	}

	public String generateRedirectURL(HttpServletRequest req) {
		Announcement an = null;
		String amazonId = null;
		try {
			if (!isValidRedirectUrl(req)) {

				return null;
			}
			
			String announcementId = req.getParameter("announcement");
			
			if(!Util.stringIsValid(announcementId)){
				 announcementId = req.getParameter("a");
			}
			
			an = announcementMgr.getAnnouncementById(Long.parseLong(announcementId));
			Product product = productMgr.getProductById(an.getProductId());
			
			//if (isTwitter(req) && Util.stringIsValid(product.getCustomDestination()) && (product.getCustomDestination().contains("flickr.com") || product.getCustomDestination().contains("youtube.com")) ) {
			//	return product.getCustomDestination();
			//}
			
			Click click = new Click(an.getId(), req.getRemoteAddr());
			pool.execute(new ClickTask(click));
			//logger.info("Redirection request info: " + generateRequestDataMini(req));
			String redirectLink = "";

			if (isTwitter(req) && Util.stringIsValid(product.getAlternativeDestionation())) {
				redirectLink = product.getAlternativeDestionation();
			} else {
				redirectLink = product.getDestination();
			}

			//logger.info("Redirect link: " + redirectLink);

			return redirectLink;
		} catch (Exception e) {
			logger.error("Redirect link generation failed: " + e.getMessage() + "\nCaused by:" + e.getCause() + "\nRequest data: " + generateRequestData(req) + "\nAnnouncer data: " + an);
			return null;
		}

	}

	private boolean isValidRedirectUrl(HttpServletRequest req) {
		String announcementId = req.getParameter("announcement");
		if (announcementId == null) {
			return false;
		}

		return true;
	}

	public boolean isTwitter(HttpServletRequest req) {
		String remoteAddr = req.getRemoteAddr();
		if (remoteAddr.startsWith("199.59.149.")) {
			return true;
		}
		return false;
	}

	private String generateRequestData(HttpServletRequest req) {
		String result = "";
		Enumeration attEn = req.getAttributeNames();
		while (attEn.hasMoreElements()) {
			String attName = (String) attEn.nextElement();
			String attValue = (String) req.getAttribute(attName);
			result = result + "Attr " + attName + " : " + attValue + "\n";
		}
		result = result + "Context path : " + req.getContextPath() + "\n";
		result = result + "Method : " + req.getMethod() + "\n";
		Enumeration paramEn = req.getParameterNames();
		while (paramEn.hasMoreElements()) {
			String paramName = (String) paramEn.nextElement();
			String paramValue = (String) req.getParameter(paramName);
			result = result + "Param " + paramName + " : " + paramValue + "\n";
		}
		result = result + "Path info : " + req.getPathInfo() + "\n";
		result = result + "Path translated : " + req.getPathTranslated() + "\n";
		result = result + "Query String : " + req.getQueryString() + "\n";
		result = result + "Remote Addr : " + req.getRemoteAddr() + "\n";
		result = result + "Remote Host : " + req.getRemoteHost() + "\n";
		result = result + "Remote User : " + req.getRemoteUser() + "\n";
		result = result + "Request uri : " + req.getRequestURI() + "\n";
		result = result + "Server name : " + req.getServerName() + "\n";
		result = result + "Servlet path : " + req.getServletPath() + "\n";
		return result;
	}

	private String generateRequestDataMini(HttpServletRequest req) {
		return "Remote Addr: " + req.getRemoteAddr() + " AnnouncementId: " + req.getParameter("announcement");
	}

}
