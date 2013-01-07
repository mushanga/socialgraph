package com.amazonbird.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.db.data.Announcement;

public class AnnouncementResource {
	@Context
	UriInfo uriInfo;
	@Context
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();

	Request request;
	long id;

	public AnnouncementResource(UriInfo uriInfo, Request request, long id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	// Application integration
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Announcement getAnnouncement() {
		Announcement announcement = announcementMgr.getAnnouncementById(id);
		if (announcement == null)
			throw new RuntimeException("Get: Announcement with " + announcement.getId() + " not found");
		return announcement;
	}

	// For the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public Announcement getAnnouncementHTML() {
		Announcement announcement = announcementMgr.getAnnouncementById(id);
		if (announcement == null)
			throw new RuntimeException("Get: Announcement with " + id + " not found");
		return announcement;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putAnnouncement(JAXBElement<Announcement> announcement) {
		Announcement c = announcement.getValue();
		return putAndGetResponse(c);
	}

	@DELETE
	public void deleteAnnouncement() {
		announcementMgr.removeAnnouncement(id);
	}

	private Response putAndGetResponse(Announcement announcement) {
		Response res;

		res = Response.created(uriInfo.getAbsolutePath()).build();

		announcementMgr.addAnnouncement(announcement);
		return res;
	}

}
