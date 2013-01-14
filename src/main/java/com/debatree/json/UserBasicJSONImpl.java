package com.debatree.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.neo4j.graphdb.Node;

import com.amazonbird.util.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tcommerce.graph.GraphDataObjectIF;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBasicJSONImpl implements GraphDataObjectIF {
	private String lang;

	@Expose
	@SerializedName("screen_name")
	private String screenName = null;
	
	@Expose
	private long id = -1;

	public String getScreenName() {
		return screenName;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLang() {
		return lang;
	}

	@Override
	public void getDataFromGDBNode(Node node) {

		try {
			this.setId((Long) node.getProperty("id"));
		} catch (Exception e1) {
			
		}
		try {
			this.setScreenName((String) node.getProperty("screen_name"));
		} catch (Exception e) {
			
		}

	}

	@Override
	public void setDataToGDBNode(Node node) {
		

			try {
				node.setProperty("id", this.getId());
			} catch (Exception e) {
				
			}
		
		
			try {
				node.setProperty("screen_name", this.getScreenName());
			} catch (Exception e) {
				
			}
		

	}

}
