package com.tcommerce.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;

import com.amazonbird.config.PropsConfigMgrImpl;
import com.amazonbird.util.Util;
import com.debatree.data.User;
import com.debatree.json.UserJSONImpl;
import com.debatree.service.UserServiceImpl;
import com.debatree.task.TreeNode;
import com.debatree.task.TweetTreeNode;
import com.debatree.task.UserTreeNode;

public class GraphDatabase {

	private static Logger logger = Logger.getLogger(GraphDatabase.class);
	// Node types
	public static final String USER_ID = "user_id";
	public static final String TWEET_ID = "tweet_id";

	// Node properties
	// Node properties
	public static final String TEXT = "text";
	public static final String USER_NAME = "screen_name";

	private static GraphDatabaseService graphDatabase = null;

	private static GraphDatabase instance = new GraphDatabase();

	private GraphDatabase() {
		try {

			graphDatabase = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(System.getProperty("user.home") + "/" + PropsConfigMgrImpl.getInstance().getGraphDbPAth());
			registerShutdownHook(graphDatabase);
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	public static GraphDatabase getInstance() {
		return instance;
	}

	public void registerShutdownHook(final GraphDatabaseService graphDatabase) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDatabase.shutdown();
			}
		});
	}

	public void shutDown() {
		if (graphDatabase != null) {
			graphDatabase.shutdown();
		}
	}

	public void addBidirectionalFriendship(long id1, long id2) {
		Transaction tx = graphDatabase.beginTx();
		try {

			Index<Node> tweetsIndex = graphDatabase.index().forNodes(USER_ID);

			Node node1 = graphDatabase.createNode();

			Node existing = tweetsIndex.putIfAbsent(node1, USER_ID, id1);
			if (existing != null) {
				node1 = existing;
			}
			Node node2 = graphDatabase.createNode();

			Node existing2 = tweetsIndex.putIfAbsent(node2, USER_ID, id2);
			if (existing2 != null) {
				node2 = existing2;
			}

			node1.createRelationshipTo(node2, RelTypes.FOLLOWS);
			node2.createRelationshipTo(node1, RelTypes.FOLLOWS);

			tx.success();
		} finally {
			tx.finish();
		}

	}

	private void addFriendshipNoTx(long src, long trg) {
		if (src < 0 || trg < 0) {
			return;
		}
		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);

		Node node1 = graphDatabase.createNode();
		// user.setDataToGDBNode(node1);

		Node existing = usersIndex.putIfAbsent(node1, USER_ID, src);
		if (existing != null) {
			node1 = existing;
		}

		node1.setProperty(USER_ID, src);

		Node node2 = graphDatabase.createNode();
		// friend.setDataToGDBNode(node2);

		Node existing2 = usersIndex.putIfAbsent(node2, USER_ID, trg);
		if (existing2 != null) {
			node2 = existing2;
		}

		node2.setProperty(USER_ID, trg);

		Iterable<Relationship> a = node1.getRelationships(RelTypes.FOLLOWS, Direction.OUTGOING);

		boolean found = false;
		for (Relationship rel : a) {
			if (rel.getEndNode().getProperty(USER_ID).equals(trg)) {
				found = true;
				break;
			}
		}

		if (!found) {
			node1.createRelationshipTo(node2, RelTypes.FOLLOWS);

		}

	}

	private void addOrUpdateNodeNoTx(UserJSONImpl user) {
		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);

		Node node1 = graphDatabase.createNode();
		Node existing = usersIndex.get(USER_ID, user.getId()).getSingle();
		if (existing != null) {
			node1 = existing;
		}
		user.setDataToGDBNode(node1);

	}

	public void addFriendship(long src, long trg) {
		ArrayList<Long> trgs = new ArrayList<Long>();
		trgs.add(trg);
		addFriendships(src, trgs);

	}

	public synchronized void addFriendships(long src, List<Long> trgs) {
		Transaction tx = graphDatabase.beginTx();
		try {
			for (Long trg : trgs) {

				addFriendshipNoTx(src, trg);
			}

			tx.success();

		} finally {
			tx.finish();
		}

	}

	public void addOrUpdateNode(UserJSONImpl user) {
		UserServiceImpl userSvc = new UserServiceImpl();

		User newUser = new User();
		user.setDataToDBObject(newUser);
		// userSvc.saveUser(newUser);
		//
		// Transaction tx = graphDatabase.beginTx();
		// try {
		// addOrUpdateNodeNoTx(user);
		// tx.success();
		//
		// } finally {
		// tx.finish();
		// }
	}

	public List<Long> getFriends(long srcId) {
		ArrayList<Long> friends = new ArrayList<Long>();
		try {

			Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
			Node node = usersIndex.get(USER_ID, srcId).getSingle();

			if (node != null) {
				Expander expander = Traversal.expanderForTypes(RelTypes.FOLLOWS, Direction.OUTGOING);

				Iterable<Relationship> nodes = expander.expand(node);
				for (Relationship rel : nodes) {
					// UserJSONImpl user = new UserJSONImpl();
					// user.getDataFromGDBNode(rel.getEndNode());
					friends.add((Long) rel.getEndNode().getProperty(USER_ID));

				}

			}
		} catch (Exception ex) {
			System.out.print(ex);
		}
		return friends;

	}

	public List<Long> getMutualFriends(long srcId) {
		return getMutualFriendsInList(srcId, null);

	}

	public List<Long> getMutualFriendsInList(long srcId, List<Long> ids) {
		ArrayList<Long> friends = new ArrayList<Long>();

		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
		Node node = usersIndex.get(USER_ID, srcId).getSingle();
		Iterable<Relationship> rels = node.getRelationships(Direction.BOTH);

		ArrayList<String> links = new ArrayList<String>();
		if (rels != null) {
			for (Relationship rel : rels) {
				long id1 = (Long) rel.getStartNode().getProperty(USER_ID);
				long id2 = (Long) rel.getEndNode().getProperty(USER_ID);
				if (!Util.isListValid(ids) || (ids.contains(id1) && ids.contains(id2))) {

					String startId = String.valueOf(id1);
					String endId = String.valueOf(id2);
					links.add(startId + "-" + endId);
				}
			}
		}

		for (int i = 0; i < links.size(); i++) {
			String link = links.get(i);
			Long id1 = Long.valueOf(link.split("-")[0]);
			Long id2 = Long.valueOf(link.split("-")[1]);
			String mirrored = id2 + "-" + id1;

			if (links.contains(mirrored)) {

				if (srcId == id1) {
					friends.add(id2);
				} else {
					friends.add(id1);
				}

				links.remove(mirrored);
			}

		}

		return friends;

	}

	public List<UserJSONImpl> getUsers(List<Long> userIdArray) {
		ArrayList<UserJSONImpl> users = new ArrayList<UserJSONImpl>();
		try {

			IndexManager usersIndexMgr = graphDatabase.index();
			for (Long userId : userIdArray) {
				Node userNode = usersIndexMgr.forNodes(USER_ID).get(USER_ID, userId).getSingle();
				if (userNode != null) {
					UserJSONImpl user = new UserJSONImpl();
					user.getDataFromGDBNode(userNode);
					users.add(user);
				}

			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return users;

	}

	public UserJSONImpl getUserByName(String name) {
		UserServiceImpl userSvc = new UserServiceImpl();

		UserJSONImpl newUser = new UserJSONImpl();
		try {
			newUser.getDataFromDBObject(userSvc.getUserByName(name));
		} catch (Exception ex) {
			newUser = null;
		}
		return newUser;
		// UserJSONImpl user = null;
		// try {
		//
		// IndexManager usersIndexMgr = graphDatabase.index();
		//
		// for ( Node userNode : usersIndexMgr.forNodes(USER_ID).query(
		// "screen_name:"+name ) )
		// {
		// user = new UserJSONImpl();
		// user.getDataFromGDBNode(userNode);
		// // This will return "The Matrix" from 1999 only.
		// }
		//
		//
		// } catch (Exception ex) {
		// logger.error(ex.getMessage(), ex);
		// }
		// return user;

	}

	public void addTweetTree(TweetTreeNode dtn) {
		Transaction tx = graphDatabase.beginTx();
		try {

			addTreeNode(dtn.getRoot(), TWEET_ID, RelTypes.REPLIES, Direction.INCOMING);
			tx.success();
		} finally {
			tx.finish();
		}

	}

	public void addUserTree(UserTreeNode dtn) {
		Transaction tx = graphDatabase.beginTx();
		try {

			addTreeNode(dtn.getRoot(), USER_ID, RelTypes.FOLLOWS, Direction.OUTGOING);
			tx.success();
		} finally {
			tx.finish();
		}

	}

	private void addTreeNode(TreeNode treeNode, String type, RelationshipType parentChildRelation, Direction direction) {

		Index<Node> tweetsIndex = graphDatabase.index().forNodes(type);

		Node node = graphDatabase.createNode();

		Node existing = tweetsIndex.putIfAbsent(node, type, treeNode.getId());
		if (existing != null) {
			node = existing;
		}
		treeNode.setProperties(node);
		TreeNode parent = treeNode.getParent();

		if (parent != null) {
			Node parentNode = tweetsIndex.get(type, parent.getId()).getSingle();
			if (direction == Direction.OUTGOING) {
				parentNode.createRelationshipTo(node, parentChildRelation);
			} else if (direction == Direction.INCOMING) {
				node.createRelationshipTo(parentNode, parentChildRelation);
			} else {
				parentNode.createRelationshipTo(node, parentChildRelation);
				node.createRelationshipTo(parentNode, parentChildRelation);
			}

		}

		if (treeNode.getChildren() != null) {
			for (TreeNode childOfChild : treeNode.getChildren()) {
				addTreeNode(childOfChild, type, parentChildRelation, direction);
			}
		}

	}

	public TweetTreeNode getDebateTreeByTweetId(long id) {
		TweetTreeNode dtn = null;
		try {
			Index<Node> tweetsIndex = graphDatabase.index().forNodes(TWEET_ID);
			Node node = tweetsIndex.get(TWEET_ID, id).getSingle();

			dtn = new TweetTreeNode();
			dtn.setProperties(node);
			getTweetTreeNodes(dtn);
		} catch (Exception ex) {

		}

		return dtn;

	}

	public UserTreeNode getTreeByUserId(long id) {
		UserTreeNode dtn = null;
		try {
			Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
			Node node = usersIndex.get(USER_ID, id).getSingle();

			dtn = new UserTreeNode();
			dtn.setProperties(node);
			getUserTreeNodes(dtn);
		} catch (Exception ex) {
			dtn = null;
		}

		return dtn;

	}

	private void getTweetTreeNodes(TweetTreeNode dtn) {

		try {

			Index<Node> tweetsIndex = graphDatabase.index().forNodes(TWEET_ID);
			Node node = tweetsIndex.get(TWEET_ID, dtn.getId()).getSingle();
			if (node != null) {
				Expander expander = Traversal.expanderForTypes(RelTypes.REPLIES, Direction.INCOMING);

				Iterable<Relationship> nodes = expander.expand(node);
				for (Relationship rel : nodes) {

					TweetTreeNode dtnc = new TweetTreeNode();
					dtnc.setProperties(rel.getStartNode());
					dtnc.setParent(dtn);
					dtn.addChild(dtnc);

					getTweetTreeNodes(dtnc);
				}

			} else {
				return;
			}

		} catch (Exception ex) {
			int a = 5;
			a++;
		}

	}

	private void getUserTreeNodes(UserTreeNode dtn) {

		try {

			Index<Node> tweetsIndex = graphDatabase.index().forNodes(USER_ID);
			Node node = tweetsIndex.get(USER_ID, dtn.getId()).getSingle();
			if (node != null) {
				Expander expander = Traversal.expanderForTypes(RelTypes.FOLLOWS, Direction.OUTGOING);

				Iterable<Relationship> nodes = expander.expand(node);
				for (Relationship rel : nodes) {

					UserTreeNode dtnc = new UserTreeNode();
					dtnc.setProperties(rel.getStartNode());
					dtnc.setParent(dtn);
					dtn.addChild(dtnc);

					getUserTreeNodes(dtnc);
				}

			} else {
				return;
			}

		} catch (Exception ex) {
			int a = 5;
			a++;
		}

	}

	public static int findConnectionBetweet(long from, long to) {
		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
		Node fromNode = usersIndex.get(USER_ID, from).getSingle();
		Node toNode = usersIndex.get(USER_ID, to).getSingle();
		if (fromNode != null && toNode != null) {
			Expander expander = Traversal.expanderForTypes(RelTypes.RETWEETS, Direction.OUTGOING);
			PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(expander, 5);
			Path path = pathFinder.findSinglePath(fromNode, toNode);
			return path == null ? -1 : path.length();
		} else {
			return -1;
		}

	}

	private static enum RelTypes implements RelationshipType {
		RETWEETS, REPLIES, FOLLOWS, FAVORITES
	}

}
