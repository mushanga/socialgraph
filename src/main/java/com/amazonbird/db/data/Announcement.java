/**
	TwitStreet - Twitter Stock Market Game
    Copyright (C) 2012  Engin Guller (bisanthe@gmail.com), Cagdas Ozek (cagdasozek@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/

package com.amazonbird.db.data;

import java.sql.ResultSet;
import java.sql.Timestamp;

import java.sql.SQLException;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.amazonbird.util.Util;

@XmlRootElement
public class Announcement implements DataObjectIF {

	Util util = Util.getInstance();

	public static String STATUS_INITIALIZED = "Initialized";
	public static String STATUS_WAITING_FOR_CUSTOMER = "Waiting for Customer";
	public static String STATUS_SEARCHING_FOR_CUSTOMER = "Searching for Customer";
	public static String STATUS_WAITING_FOR_ANNOUNCER = "Waiting for Announcer";
	public static String STATUS_READY = "Ready";
	public static String STATUS_SENDING = "Sending...";
	public static String STATUS_ERROR = "Error";
	public static String STATUS_SENT = "Sent";
	
	public static String[] STATUSES = {STATUS_INITIALIZED,STATUS_WAITING_FOR_CUSTOMER,
		STATUS_SEARCHING_FOR_CUSTOMER,STATUS_WAITING_FOR_ANNOUNCER,STATUS_READY,
		STATUS_SENDING,STATUS_ERROR,STATUS_SENT};
	
	long id;
	long customerId;
	long announcerId;
	String amazonTag;
	Timestamp timeSent;
	int clickCount;
	long productId;
	String message;
	long reasonId;
	private long tweetId;
	Timestamp timeModified;
	String status;
	long causedByStatusId;

	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		this.setId(rs.getLong("id"));
		this.setCustomerId(rs.getLong("customerid"));
		this.setAnnouncerId(rs.getLong("announcerid"));
		this.setProductId(rs.getLong("productid"));
		this.setReasonId(rs.getLong("reasonid"));
		this.setAmazonTag(rs.getString("amazontag"));
		this.setMessage(rs.getString("message"));
		this.setStatus(rs.getString("status"));
		this.setTimeSent(rs.getTimestamp("timesent"));
		this.setTimeModified(rs.getTimestamp("timemodified"));
		this.setClickCount(rs.getInt("clickcount"));
		this.setTweetId(rs.getLong("tweetid"));
	}

	public String getTimeSentStr() {
		String timeSent = "";
		if (this.timeSent != null) {
			timeSent = util.dateToString(new Date(this.timeSent.getTime()));
		}
		return timeSent;
	}

	public String getTimeModifiedStr() {
	String timeModified = "";
		if (this.timeModified != null) {
			timeModified = util.dateToString(new Date(this.timeModified.getTime()));
		}
		return timeModified;
	}

	public String toString(){
	
	
		String str = "\n"+
		"id="+id+"\n"+
		"customerid="+customerId+"\n"+
		"announcerid="+announcerId+"\n"+
		"productid="+productId+"\n"+
		"reasonid="+reasonId+"\n"+
		"amazontag="+amazonTag+"\n"+
		"timesent="+getTimeSentStr()+"\n"+
		"timemodified="+getTimeModifiedStr()+"\n"+
		"clickcount="+clickCount+"\n"+
		"message="+message+"\n"+
		"tweetid="+tweetId+"\n"+
		"status="+status+"\n";
		return str;
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long userId) {
		this.customerId = userId;
	}

	public long getAnnouncerId() {
		return announcerId;
	}

	public void setAnnouncerId(long announcerId) {
		this.announcerId = announcerId;
	}

	public String getAmazonTag() {
		return amazonTag;
	}

	public void setAmazonTag(String amazonTag) {
		this.amazonTag = amazonTag;
	}

	public Timestamp getTimeSent() {
		return timeSent;
	}

	public void setTimeSent(Timestamp timeSent) {
		this.timeSent = timeSent;
	}

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getReasonId() {
		return reasonId;
	}

	public void setReasonId(long reasonId) {
		this.reasonId = reasonId;
	}

	public Timestamp getTimeModified() {
		return timeModified;
	}

	public void setTimeModified(Timestamp timeModified) {
		this.timeModified = timeModified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Announcement() {
	
	}

	public long getTweetId() {
		return tweetId;
	}


	public void setTweetId(long tweetId) {
		this.tweetId = tweetId;
	}

	public boolean isSent(){
		return this.status.equalsIgnoreCase(STATUS_SENT);
	}
}