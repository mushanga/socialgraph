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
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlRootElement;

import com.amazonbird.amazonproxy.AmazonProduct;

@XmlRootElement
public class Product implements DataObjectIF {
	long id;	
	private long announcerId;
	String name;
	long dateAdded;	
	double price;
	private String destination;
	private String alternativeDestionation;
	private String locale;
	private boolean active;
	private String[] pictureUrls;
	private int relation2User = -1;
	public Product(long id, String name, long dateAdded, double price, String destionation, String alternativeDestination, String locale,long announcerId) {
		super();
		this.id = id;
		this.announcerId = announcerId;
		this.name = name;
		this.dateAdded = dateAdded;
		this.price = price;
		this.destination = destionation;
		this.alternativeDestionation = alternativeDestination; 
		this.locale = locale;
	}
	
	public Product(AmazonProduct amazonProduct){
		this.name = amazonProduct.getTitle();
		this.price = amazonProduct.getPrice();
	}

	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		this.setId(rs.getLong("id"));
		this.setAnnouncerId(rs.getLong("announcerid"));
		this.setName(rs.getString("name"));
		this.setDateAdded(rs.getTimestamp("dateadded").getTime());
		this.setPrice(rs.getDouble("price"));
		this.setDestination(rs.getString("destination"));
		this.setAlternativeDestionation(rs.getString("alternativeDestination"));
		this.setLocale(rs.getString("locale"));
		this.setActive(rs.getBoolean("active"));
	}

	public String toString(){
		
		String str = "\n"+
		"id="+id+"\n"+
		"name="+name+"\n"+
		"dateadded="+dateAdded+"\n"+
		"price="+price+"\n"+
		"customdestination="+destination+"\n"+
		"active="+active+"\n";
		return str;
		
	}
	

	public Product() {
		// TODO Auto-generated constructor stub
	}
	public long getId() {
		return id;
	}




	public void setId(long id) {
		this.id = id;
	}

	public long getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(long dateAdded) {
		this.dateAdded = dateAdded;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the customDestination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param customDestination the customDestination to set
	 */
	public void setDestination(String customDestination) {
		this.destination = customDestination;
	}

	public String getAlternativeDestionation() {
		return alternativeDestionation;
	}

	public void setAlternativeDestionation(String alternativeDestionation) {
		this.alternativeDestionation = alternativeDestionation;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the pictureUrls
	 */
	public String[] getPictureUrls() {
		return pictureUrls;
	}

	/**
	 * @param pictureUrls the pictureUrls to set
	 */
	public void setPictureUrls(String[] pictureUrls) {
		this.pictureUrls = pictureUrls;
	}

	/**
	 * @return the announcerId
	 */
	public long getAnnouncerId() {
		return announcerId;
	}

	/**
	 * @param announcerId the announcerId to set
	 */
	public void setAnnouncerId(long announcerId) {
		this.announcerId = announcerId;
	}

	public int getRelation2User() {
		return relation2User;
	}

	public void setRelation2User(int relation2User) {
		this.relation2User = relation2User;
	}



}