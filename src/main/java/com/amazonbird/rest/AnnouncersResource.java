//COMMENTO



package com.amazonbird.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;

import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.db.data.Comment;
import com.amazonbird.util.Util;

@Path("/announcers")
public class AnnouncersResource {

	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	
	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private static Logger logger = Logger.getLogger(AnnouncersResource.class);

	Util util = Util.getInstance();

	// Return the list of announcers
	@GET
	@Produces({ MediaType.TEXT_XML })
	public List<Announcer> getAnnouncers() {
		return announcerMgr.getAllAnnouncers();
	}

	// Return the list of announcers
	@GET
	@Path("active")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Announcer> getAnnouncersIDs() {
		return announcerMgr.getActiveAnnouncers();
	}

	// retuns the number of announcers
	// Use http://localhost:8080/amazonbird/announcers/count
	// to get the total number of records
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = announcerMgr.getAllAnnouncers().size();
		return String.valueOf(count);
	}

	@PUT
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newAnnouncer(@FormParam("id") long id, @FormParam("screenName") String screenName, @FormParam("consumerKey") String consumerKey, @FormParam("consumerSecret") String consumerSecret, @FormParam("accessToken") String accessToken, @FormParam("accessTokenSecret") String accessTokenSecret, @FormParam("name") String name, @FormParam("surname") String surname, @FormParam("email") String email, @FormParam("password") String password, @Context HttpServletResponse servletResponse) throws IOException {
		Announcer announcer = new Announcer();
		announcer.setId(id);
		announcer.setScreenName(screenName);
		announcer.setConsumerKey(consumerKey);
		announcer.setConsumerSecret(consumerSecret);
		announcer.setAccessToken(accessToken);
		announcer.setAccessTokenSecret(accessTokenSecret);
		announcer.setName(name);
		announcer.setSurname(surname);
		announcer.setEmail(email);
		announcer.setPassword(password);
		announcerMgr.addAnnouncer(announcer);

		servletResponse.sendRedirect(util.getContextPath() + "/mgr/announcer/details?id=" + announcer.getId());
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateAnnouncer(@FormParam("id") long id, @FormParam("screenName") String screenName, @FormParam("consumerKey") String consumerKey, @FormParam("consumerSecret") String consumerSecret, @FormParam("accessToken") String accessToken, @FormParam("accessTokenSecret") String accessTokenSecret, @FormParam("name") String name, @FormParam("surname") String surname, @FormParam("email") String email, @FormParam("password") String password, @Context HttpServletResponse servletResponse) throws IOException {
		Announcer announcer = new Announcer();
		announcer.setId(id);
		announcer.setScreenName(screenName);
		announcer.setConsumerKey(consumerKey);
		announcer.setConsumerSecret(consumerSecret);
		announcer.setAccessToken(accessToken);
		announcer.setAccessTokenSecret(accessTokenSecret);
		announcer.setName(name);
		announcer.setSurname(surname);
		announcer.setEmail(email);
		announcer.setPassword(password);
		announcerMgr.updateAnnouncer(announcer);
		servletResponse.sendRedirect(util.getContextPath() + "/mgr/announcer/details?id=" + announcer.getId());
	}
	

	@Path("config")
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateConfig(@FormParam(AnnouncerMgrImpl.CONFIG_ANNOUNCE_PERIOD) String announcePeriod, @FormParam(AnnouncerMgrImpl.CONFIG_ACTION_PERIOD) String actionPeriod, @FormParam(AnnouncerMgrImpl.CONFIG_RETWEET_FAVORITE_PERIOD) String retweetFavoritePeriod, @FormParam(AnnouncerMgrImpl.CONFIG_FOLLOW_PERIOD) String followPeriod, @Context HttpServletResponse servletResponse) throws IOException {
//		announcerMgr.setActionPeriodInMins(Integer.valueOf(actionPeriod));
//		announcerMgr.setRetweetFavoritePeriodInMins(Integer.valueOf(retweetFavoritePeriod));
//		announcerMgr.setFollowPeriodInMins(Integer.valueOf(followPeriod));
//		announcerMgr.setAnnouncePeriodInMins(Integer.valueOf(announcePeriod));

		logger.info("Announcer Config Updated" + "\n" + "Action: " + actionPeriod + " (mins)\n" + "Retweet/Favorite: " + retweetFavoritePeriod + " (mins)\n" + "Follow: " + followPeriod + " (mins)\n" + "Announce: " + announcePeriod + " (mins)\n");

		servletResponse.sendRedirect(util.getContextPath() + "/mgr/announcer/config");
	}

	// Defines that the next path parameter after announcers is
	// treated as a parameter and passed to the AnnouncerResources
	// Allows to type http://localhost:8080/amazonbird/announcers/1
	// 1 will be treaded as parameter announcer and passed to AnnouncerResource
	@Path("{announcerId}")
	public AnnouncerResource getAnnouncer(@PathParam("announcerId") long id) {
		return new AnnouncerResource(uriInfo, request, id);
	}
	
//	@Path("comment")
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Comment addComment(Comment comment){
//		comment.setDateAdded(Calendar.getInstance().getTime());
//		return announcerMgr.addComment(comment);
//	}
	

}
