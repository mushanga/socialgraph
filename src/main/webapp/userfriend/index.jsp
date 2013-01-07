
<%@page import="com.google.gson.reflect.TypeToken"%>
<%@page import="java.lang.reflect.Type"%>
<%@page import="java.util.List"%>
<%@page import="com.debatree.task.UserTreeNode"%>
<%@page import="com.google.gson.GsonBuilder"%>
<%@page import="com.debatree.json.IDsJSONImpl"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.debatree.json.UserJSONImpl"%>
<%@page import="com.debatree.twitter.TwitterClient"%>

<%@page import="com.debatree.task.FindDebateTask"%>
<%
response.setContentType("application/json");
	String userName = request.getParameter("user");
long userId =0;
try{

userId = Long.valueOf(request.getParameter("user_id"));
}catch(Exception ex){
	
}

	TwitterClient tc = TwitterClient.getDefaultClient();
	if(userName==null){

		userName = tc.getUser(userId).getScreenName();
	}
	String json  = tc.getFriendsAsJSON(userName);

%>
<%=json%>

