package com.amazonbird.db.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Comment implements DataObjectIF{
	long id;
	Date dateAdded;
	String comment;
	long announcerId;
	long productId;
	String screenName;
	String longName;
	String pictureUrl;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public long getAnnouncerId() {
		return announcerId;
	}
	public void setAnnouncerId(long announcerId) {
		this.announcerId = announcerId;
	}
	
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getLongName() {
		return longName;
	}
	public void setLongName(String longName) {
		this.longName = longName;
	}
	public String getPictureUrl() {
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	@Override
	public String toString(){
		return new StringBuilder("id: ").append(id).append("\n")
		.append("comment: ").append(comment).append("\n")
		.append("announcerId: ").append(announcerId).append("\n")
		.append("dateAdded: ").append(dateAdded).append("\n").toString();
	}
	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		this.comment = rs.getString("comment");
		this.announcerId = rs.getLong("announcerId");
		this.dateAdded = new Date(rs.getDate("dateAdded").getTime());
		this.longName = rs.getString("longName");
		this.screenName = rs.getString("screenName");
		this.pictureUrl = rs.getString("pictureUrl");
	}
}
