package com.amazonbird.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.amazonbird.amazonproxy.AmazonProxy;
import com.amazonbird.amazonproxy.AmazonProxyImpl;
import com.amazonbird.announce.MessageMgrImpl;
import com.amazonbird.db.data.Message;

public class MessageResource {
	@Context
	UriInfo uriInfo;
	@Context
	MessageMgrImpl messageMgr = MessageMgrImpl.getInstance();
	@Context
	AmazonProxy amazonProxy = AmazonProxyImpl.getInstance();
	Request request;
	String id;

	public MessageResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	// Application integration
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Message getMessage() {
		Message message = messageMgr.getMessageById(id);
		if (message == null)
			throw new RuntimeException("Get: Message with " + id + " not found");
		return message;
	}

	// For the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public Message getMessageHTML() {
		Message message = messageMgr.getMessageById(id);
		if (message == null)
			throw new RuntimeException("Get: Message with " + id + " not found");
		return message;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putMessage(JAXBElement<Message> message) {
		Message c = message.getValue();
		return putAndGetResponse(c);
	}

	@DELETE
	public void deleteMessage() {
		messageMgr.removeMessage(Long.valueOf(id));

	}
	
	private Response putAndGetResponse(Message message) {
		Response res;

		res = Response.created(uriInfo.getAbsolutePath()).build();

		messageMgr.addMessage(message);
		return res;
	}

}
