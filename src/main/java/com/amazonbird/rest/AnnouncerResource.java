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

import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.db.data.Announcer;

public class AnnouncerResource {
	@Context
	UriInfo uriInfo;
	@Context
	

	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	
	
	Request request;
	long id;
	public AnnouncerResource(UriInfo uriInfo, Request request, long id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}
	
	//Application integration 		
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Announcer getAnnouncer() {
		Announcer announcer = announcerMgr.getAnnouncer(id);
		if(announcer==null)
			throw new RuntimeException("Get: Announcer with " + announcer.getId() +  " not found");
		return announcer;
	}
	
	// For the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public Announcer getAnnouncerHTML() {
		Announcer announcer = announcerMgr.getAnnouncer(id);
		if(announcer==null)
			throw new RuntimeException("Get: Announcer with " + id +  " not found");
		return announcer;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putAnnouncer(JAXBElement<Announcer> announcer) {
		Announcer c = announcer.getValue();
		return putAndGetResponse(c);
	}
	
	@DELETE
	public void deleteAnnouncer() {
		announcerMgr.removeAnnouncer(id);
	}
	
	private Response putAndGetResponse(Announcer announcer) {
		Response res;
		if(announcerMgr.containsAnnouncer(announcer.getId())) {
			res = Response.noContent().build();
		} else {
			res = Response.created(uriInfo.getAbsolutePath()).build();
		}
		announcerMgr.addAnnouncer(announcer);
		return res;
	}
	
	

}
