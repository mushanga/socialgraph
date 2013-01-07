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
public class Reason implements DataObjectIF {

	public static int TYPE_FOLLOW = 1;
	public static int TYPE_TWEET = 2;
	public static int TYPE_HASH = 3;
	public static int TYPE_MENTION = 4;
	public static int TYPE_STREAM = 5;
	

	public static int[] reasonTypeIds = {1,2,3,4,5};
	public static String[] reasonTypes = {"Follow","Tweet","Hash","Mention","Stream"};
	
	
	long id;
	int type;
	String value;
	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		this.setId(rs.getLong("id"));
		this.setType(rs.getInt("type"));
		this.setValue(rs.getString("value"));
	
	
	}




	public String toString(){
		
		String str = "\n"+
		"id="+id+"\n"+
		"type="+type+"\n"+
		"value="+value+"\n";
		return str;
		
	}
	

	public Reason() {
		// TODO Auto-generated constructor stub
	}




	public long getId() {
		return id;
	}




	public void setId(long id) {
		this.id = id;
	}




	public int getType() {
		return type;
	}




	public void setType(int type) {
		this.type = type;
	}




	public String getValue() {
		return value;
	}




	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription(){
		
		
		String ret = "";
		
		if(TYPE_TWEET == type){
			ret = "Tweet";
		}else if(TYPE_FOLLOW == type){
			ret = "Follow";
		}else if(TYPE_HASH == type){
			ret = "Hash";
		}else if(TYPE_MENTION == type){
			ret = "Mention";
		}else if(TYPE_STREAM == type){
			ret = "Stream";
		}
		return ret + " "+value;
	}
}