package com.debatree.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import twitter4j.User;

import com.debatree.json.UserJSONImpl;
import com.debatree.twitter.TwitterClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class GraphState{
	
	
	HashMap<Long, ArrayList<Long>> links = new HashMap<Long, ArrayList<Long>>();
	ArrayList<Long> nodes = new ArrayList<Long>();
	
	@Expose
	@SerializedName("links")
	HashMap<Long, ArrayList<Long>> visibleLinks = new HashMap<Long, ArrayList<Long>>();
	
	@Expose
	@SerializedName("nodes")
	ArrayList<Long> visibleNodes = new ArrayList<Long>();

	@Expose
	List<UserJSONImpl> users= new ArrayList<UserJSONImpl>();

	@Expose
	private
	long user = -1;

	@Expose
	private
	int total;

	@Expose
	private
	int reloadTimeInSecs;

	@Expose
	private
	int left;

	@Expose
	private
	boolean protectedGraph;


	private static Logger logger = Logger.getLogger(GraphState.class);
	
	private void addVisibleLink(long src, long trg){		
		
		addVisibleNode(src);
		addVisibleNode(trg);
		if(!visibleLinks.containsKey(src)){
			visibleLinks.put(src, new ArrayList<Long>());
		}
		
		ArrayList<Long> srcLinks = visibleLinks.get(src);
		if(!srcLinks.contains(trg)){
			srcLinks.add(trg);
		}
	}
	public void addLink(long src, long trg){		
		
		addNode(src);
		addNode(trg);
		if(!links.containsKey(src)){
			links.put(src, new ArrayList<Long>());
		}
		
		ArrayList<Long> srcLinks = links.get(src);
		if(!srcLinks.contains(trg)){
			srcLinks.add(trg);
		}
	}
	public void addNode(long id){
		if(!nodes.contains(id)){
			nodes.add(id);
		}
		
	}
	public void addVisibleNode(long id){
		if(!visibleNodes.contains(id)){
			visibleNodes.add(id);
		}
		
	}
	public void process() {
		
		Set<Long> linkNodesSet = links.keySet();
		
		for(Long nodeId : linkNodesSet){
			ArrayList<Long> friendIds = links.get(nodeId);
			for(Long friendId : friendIds){
				
				if(links.containsKey(friendId)&& links.get(friendId).contains(nodeId)){
					addVisibleLink(nodeId, friendId);
				}
						
			}
		}
		try {
			users = TwitterClient.getDefaultClient().getUsers(visibleNodes);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		
		
		
	}
	public String toJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		
		String json = gson.toJson(this, this.getClass());
		return json;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getReloadTimeInSecs() {
		return reloadTimeInSecs;
	}
	public void setReloadTimeInSecs(int reloadTimeInSecs) {
		this.reloadTimeInSecs = reloadTimeInSecs;
	}
	public long getUser() {
		return user;
	}
	public void setUser(long user) {
		this.user = user;
	}
	public boolean isProtectedGraph() {
		return protectedGraph;
	}
	public void setProtectedGraph(boolean protectedGraph) {
		this.protectedGraph = protectedGraph;
	}
	
}
