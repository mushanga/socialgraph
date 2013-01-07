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

@XmlRootElement
public class Message implements DataObjectIF {

	public static int TYPE_AMAZON = 0;
	public static int TYPE_QURAN_SEARCH = 1;
	
	
	long id;
	private boolean active;
	private String text;
	private String textWithParametersAdded;
	public Message(long id, String text, boolean active) {
		super();
		this.active = active;
		this.text = text;
		this.id = id;
	}
	
	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		this.setId(rs.getLong("id"));
		this.setText(rs.getString("text"));
		this.setActive(rs.getBoolean("active"));
	}


	public String toString(){
		
		String str = "\n"+
		"id="+id+"\n"+
		"text="+getText()+"\n"+
		"active="+active+"\n";
		return str;
		
	}
	

	public Message() {
		// TODO Auto-generated constructor stub
	}
	public long getId() {
		return id;
	}




	public void setId(long id) {
		this.id = id;
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
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	public String getTextWithParametersAdded(){
		
		return this.textWithParametersAdded;
		
	}

	/**
	 * @param textWithParametersAdded the textWithParametersAdded to set
	 */
	public void setTextWithParametersAdded(String textWithParametersAdded) {
		this.textWithParametersAdded = textWithParametersAdded;
	}


}