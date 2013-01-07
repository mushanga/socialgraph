package com.amazonbird.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.AnnouncementListCriteria;
import com.amazonbird.util.Util;

@Path("/announcements")
public class AnnouncementsResource {


	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	
	// Allows to insert contextual objects into the class, 
		// e.g. ServletContext, Request, Response, UriInfo
		@Context
		UriInfo uriInfo;
		@Context
		Request request;

		Util util = Util.getInstance();



		// Return the list of announcements 
		@GET
		@Produces({MediaType.TEXT_XML})
		public List<Announcement> getAnnouncements() {
			List<Announcement> announcements = new ArrayList<Announcement>();
			
			
			AnnouncementListCriteria alc = new AnnouncementListCriteria();
			announcements.addAll(announcementMgr.getAnnouncements(alc));
			return announcements; 
		}
		
		
		
		// retuns the number of announcements
		// Use http://localhost:8080/amazonbird/announcements/count
		// to get the total number of records
		@GET
		@Path("count")
		@Produces(MediaType.TEXT_PLAIN)
		public String getCount() {
			int count =announcementMgr.getAnnouncementCount();
			return String.valueOf(count);
		}
//
		
		
		// Defines that the next path parameter after announcements is
		// treated as a parameter and passed to the AnnouncementResources
		// Allows to type http://localhost:8080/amazonbird/announcements/1
		// 1 will be treaded as parameter announcement and passed to AnnouncementResource
		@Path("{announcementId}")
		public AnnouncementResource getAnnouncement(@PathParam("announcementId") long id) {
			return new AnnouncementResource(uriInfo, request, id);
		}
	

	
}
