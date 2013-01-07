package com.debatree.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.neo4j.graphdb.Node;

import com.google.gson.annotations.SerializedName;
import com.tcommerce.graph.GraphDataObjectIF;


@JsonIgnoreProperties(ignoreUnknown=true)
public class UserBasicJSONImpl  implements GraphDataObjectIF{
	private String lang;

    @SerializedName("screen_name")
    @JsonProperty("screen_name")
	private String screenName;
	private long id;
	
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

		this.setId((Long) node.getProperty("id"));
		this.setScreenName((String) node.getProperty("screen_name"));

		
	}
	@Override
	public void setDataToGDBNode(Node node) {

		node.setProperty("id", this.getId());		
		node.setProperty("screen_name", this.getScreenName());	
	}
	



}
