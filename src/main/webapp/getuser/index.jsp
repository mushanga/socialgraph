
<%@page import="com.debatree.task.UserTreeNode"%>
<%@page import="com.google.gson.GsonBuilder"%>
<%@page import="com.debatree.json.IDsJSONImpl"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.debatree.json.UserJSONImpl"%>
<%@page import="com.debatree.twitter.TwitterClient"%>
<%@page import="com.debatree.task.FindDebateTask"%>
<%
	String userName = request.getParameter("user");
	TwitterClient tc = TwitterClient.getDefaultClient();
	UserJSONImpl user = tc.getUser(userName);

	Gson gson = new Gson();
	
	
String json  = gson.toJson(user, UserJSONImpl.class);

%>
<%=json%>

