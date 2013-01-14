<%@page import="com.debatree.main.OAuth"%>
<%@page import="com.debatree.twitter.TwitterClient"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.Date"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.FileWriter"%>
<%@page import="com.amazonbird.db.data.Announcer"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	isELIgnored="true" session="false"%>
<%@page import="com.amazonbird.announce.ProductMgrImpl"%>
<%@page import="com.amazonbird.announce.AnnouncerMgrImpl"%>
<%@page import="com.amazonbird.announce.MessageMgrImpl"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.amazonbird.db.data.Product"%>
<%@page import="com.amazonbird.db.data.ProductMessage"%>
<%@ page import="com.amazonbird.util.*"%>
<%
	

OAuth oauth = OAuth.getInstance();

Cookie[] cookies = request.getCookies();
Announcer ann = null;
if(cookies!=null){
	for(Cookie cookie : cookies){
		if(cookie.getName().equalsIgnoreCase(OAuth.COOKIE_NAME)){
			ann = AnnouncerMgrImpl.getInstance().getAnnouncerByAccessToken(cookie.getValue());
		}
	}
	
}

TwitterClient tc = null;


String screenName = "";
try{
	tc = new TwitterClient(ann.getId());

screenName = ann.getScreenName();	
}catch(Exception exz){
	 tc = TwitterClient.getDefaultClient();
}
%>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
    <link type="text/css" rel="stylesheet" href="style/newgraphstyle.css"/>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" />
  <style type="text/css">



    </style>
  </head>
  <body>
	<h2 style="text-align: center">Twitter Graph</h2>
	<div style="width: 1000px; height: 30px;text-align: center; font-size: 12px;">
		<a href="<%=oauth.getAuthUrl()%>">Sign in with Twitter</a>
		Enter a Twitter user name: 
		<input type="text" id="usernameinput" value="<%=screenName%>"/> 
		<input id="operateBtnId" type="button" onclick="getUserGraph()"
			value="Get" />
		<input id="clearBtnId" type="button" onclick="clearGraph()"
			value="Clear" />
		<div style="float:left;width:100px;" id="sliderId"></div>
		<div style="float:left;margin-left:20px;width:20px;" id="sliderValueId"></div>
		<div style="float:left;margin-left:20px;width:200px;" id="progressbar"></div>
		
	</div>
	  <div id="loadingDiv" style="width:1000px; height: 30px; text-align: center"></div>
   
    <div id="screen" style="width:1000px; height: 600px"></div>
   
    <script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
    
     <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
    <script src="http://d3js.org/d3.v3.min.js"></script>
    <script type="text/javascript" src="js/debatree.js"></script>
    <script type="text/javascript" src="js/twitterclient.js"></script>
    <script type="text/javascript" src="js/graph.js"></script>
    <script type="text/javascript" src="js/newgraph.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    
    <script type="text/javascript">
 
	graph = new NewGraph("#screen");
	tw = new TwitterClient();
	util = new Util(); 
	var slider;
  $(function() {
	  slider = $( "#sliderId" ).slider({max:3,min:2,slide: function( event, ui ) {
		  var value = ui.value;
    	graph.threshold = Math.max(value,2) ;
    	graph.update();
    	  $('#sliderValueId').html(graph.threshold);
    } });
		
		$(function() {
		    $("#progressbar").progressbar({ value: 0 });
		});
  })
    </script>
  </body>
</html>


