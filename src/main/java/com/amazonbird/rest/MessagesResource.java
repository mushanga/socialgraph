package com.amazonbird.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import com.amazonbird.amazonproxy.AmazonProxy;
import com.amazonbird.amazonproxy.AmazonProxyImpl;
import com.amazonbird.announce.MessageMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.db.data.Message;
import com.amazonbird.db.data.Reason;
import com.amazonbird.util.Util;

@Path("/messages")
public class MessagesResource {
	MessageMgrImpl messageMgr = MessageMgrImpl.getInstance();
	AmazonProxy amazonProxy = AmazonProxyImpl.getInstance();

ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	// Allows to insert contextual objects into the class, 
		// e.g. ServletContext, Request, Response, UriInfo
		@Context
		UriInfo uriInfo;
		@Context
		Request request;


		Util util = Util.getInstance();

		// Return the list of messages 
		@GET
		@Produces({MediaType.TEXT_XML})
		public List<Message> getMessages() {
			List<Message> messages = new ArrayList<Message>();
			messages.addAll(messageMgr.getAllMessages());
			return messages; 
		}
		
		
		// retuns the number of messages
		// Use http://localhost:8080/amazonbird/messages/count
		// to get the total number of records
		@GET
		@Path("count")
		@Produces(MediaType.TEXT_PLAIN)
		public String getCount() {
			int count =messageMgr.getAllMessages().size();
			return String.valueOf(count);
		}
		


		@POST
		@Produces(MediaType.TEXT_HTML)
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public void updateMessage(@FormParam("id") String id,
				@FormParam("text") String text,
				@Context HttpServletResponse servletResponse) throws IOException {
			
			
			
			messageMgr.updateMessage(Long.valueOf(id),text);
			
		
			servletResponse.sendRedirect(util.getContextPath()+ "/mgr/message/details?id="+id);
		}
		@PUT
		@Produces(MediaType.TEXT_HTML)
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public void newMessage(@FormParam("text") String text,
				@Context HttpServletResponse servletResponse) throws IOException {
			
			
			
			
			Message message = new Message(-1,text, true);
	
			long id = messageMgr.addMessage(message);
			servletResponse.sendRedirect(util.getContextPath()+ "/mgr/message/details?id="+id);
		}
		
		
		// Defines that the next path parameter after messages is
		// treated as a parameter and passed to the MessageResources
		// Allows to type http://localhost:8080/amazonbird/messages/1
		// 1 will be treaded as parameter message and passed to MessageResource
		@Path("{messageId}")
		public MessageResource getMessage(@PathParam("messageId") String id) {
			return new MessageResource(uriInfo, request, id);
		}
	

	
}
