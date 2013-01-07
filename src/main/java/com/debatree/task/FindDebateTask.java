package com.debatree.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import com.debatree.data.Tweet;
import com.debatree.exception.DebatreeException;
import com.debatree.json.TweetJSONImpl;
import com.debatree.json.UserJSONImpl;
import com.debatree.service.TweetServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcommerce.graph.GraphDatabase;

public class FindDebateTask extends TaskBase {
	String json = null;
	static FindDebateTask instance = new FindDebateTask();

	public static FindDebateTask getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "Find Debate Task";
	}

	@Override
	public void process() throws DebatreeException {

		// String userName = "LeventUzumcu";
		// List<TweetJSONImpl> tweets = tc.getUserTimeline(userName, 10, false,
		// false);
		// TweetJSONImpl tweet = tweets.get(0);
		//
		// DebateTreeNode dtn = new DebateTreeNode();
		// dtn.setName(tweet.getUser().getScreenName() + " - " +
		// tweet.getText());
		//
		// depthFirst(dtn, tweet.getId(), userName);
		// System.out.println(dtn);

	}

	public void searchReplyTree(TweetTreeNode dtn, long tweetId, String userName) throws DebatreeException {
		List<TweetJSONImpl> replies = tc.searchRepliesForTweet(tweetId, userName);

		for (TweetJSONImpl reply : replies) {
			TweetTreeNode dtnr = new TweetTreeNode();
			dtnr.setDepth(dtn.getDepth() + 1);
			dtnr.setName(reply.getFromUser() + " - " + reply.getText());
			dtnr.setId(reply.getId());
			dtnr.setTweetText(reply.getText());
			dtnr.setTweetUserId(reply.getFromUserId());
			dtnr.setTweetUserName(reply.getFromUser());
			dtnr.setParent(dtn);
			dtn.addChild(dtnr);
			searchReplyTree(dtnr, reply.getId(), reply.getFromUser());
			// if(dtnr.getChildren().size()<1 && dtnr.getDepth()<2){
			// dtn.removeChild(dtnr);
			// }

		}

	}

	public void searchReplyTreeForTweet(TweetJSONImpl tweet) throws DebatreeException {
		TweetTreeNode dtn = new TweetTreeNode();
		dtn.setName(tweet.getUser().getScreenName() + " - " + tweet.getText());
		dtn.setId(tweet.getId());
		dtn.setTweetText(tweet.getText());
		dtn.setTweetUserId(tweet.getUser().getId());
		dtn.setTweetUserName(tweet.getUser().getScreenName());
		searchReplyTree(dtn, tweet.getId(), tweet.getUser().getScreenName());
		GraphDatabase.getInstance().addTweetTree(dtn);
	}

	public void searchFriendTreeForUser(UserJSONImpl user) throws DebatreeException {
		UserTreeNode dtn = new UserTreeNode();

		dtn.setId(user.getId());
		dtn.setUserName(user.getScreenName());
		searchFriendTree(dtn, user.getId(),user.getScreenName(), 2);
		GraphDatabase.getInstance().addUserTree(dtn);
	}
	

	public void searchFriendTree(UserTreeNode dtn, long userId, String userName, int maxDepth) throws DebatreeException {
		List<Long> friends = tc.getFollowingsIDs(userId, -1);

		int i = 10;
		for (Long friend : friends) {
			if(i==0){
				break;
				
			}
			UserTreeNode dtnr = new UserTreeNode();
			dtnr.setDepth(dtn.getDepth() + 1);
			dtnr.setId(friend);
			dtnr.setUserName(tc.getUser(friend).getScreenName());
			dtnr.setParent(dtn);
			dtn.addChild(dtnr);
			if(dtnr.getDepth()<maxDepth){
				searchFriendTree(dtnr, friend,userName,maxDepth);
			}		
			i--;
		}
	}

	public String getTweetTreeAsJSON(String userName) throws DebatreeException {

		List<TweetJSONImpl> tweets = tc.getUserTimeline(userName, 10, false, false);
		TweetJSONImpl tweet = tweets.get(2);
		TweetTreeNode result = GraphDatabase.getInstance().getDebateTreeByTweetId(tweet.getId());

		if (result == null) {
			searchReplyTreeForTweet(tweet);
			result = GraphDatabase.getInstance().getDebateTreeByTweetId(tweet.getId());
			
		} 
			try {

				Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
				json = gson.toJson(result, TweetTreeNode.class);

				File file = new File("xtree.json");
				file.delete();

				FileWriter fw = new FileWriter("xtree.json", true);
				BufferedWriter br = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(br);

				long date = new Date().getTime();
				Long longDate = new Long(date);

				pw.print(json);

				pw.flush();
				pw.close();
				br.close();
				fw.close();
			}

			catch (IOException io) {
				System.out.println("FILEWRITER EXCEPTION " + io.getMessage());
			}

			catch (Exception e) {
				System.out.println(" FILEWRITER GENERIC EXCEPTION " + e.getMessage());
			}
		
		return json;
	}

	public String getUserTreeAsJSON(String userName) throws DebatreeException {
		UserJSONImpl user = tc.getUser(userName);
		UserTreeNode result = GraphDatabase.getInstance().getTreeByUserId(user.getId());

		if (result == null) {
			searchFriendTreeForUser(user);
			result = GraphDatabase.getInstance().getTreeByUserId(user.getId());

		} 
		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
			json = gson.toJson(result, UserTreeNode.class);

			File file = new File("xtree.json");
			file.delete();

			FileWriter fw = new FileWriter("xtree.json", true);
			BufferedWriter br = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(br);

			long date = new Date().getTime();
			Long longDate = new Long(date);

			pw.print(json);

			pw.flush();
			pw.close();
			br.close();
			fw.close();
		}

		catch (IOException io) {
			System.out.println("FILEWRITER EXCEPTION " + io.getMessage());
		}

		catch (Exception e) {
			System.out.println(" FILEWRITER GENERIC EXCEPTION " + e.getMessage());
		}

		return json;
	}

}
