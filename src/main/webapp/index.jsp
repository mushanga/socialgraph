<%@page import="com.debatree.main.AuthFilter"%>
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

Announcer ann = null;
String screenName = "";

if(request.getAttribute(AuthFilter.SESS_USER)!=null){
	 ann = (Announcer) request.getAttribute(AuthFilter.SESS_USER);
}



try{
	screenName = ann.getScreenName();	
}catch(Exception exz){
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
	
<%-- 	<% if(ann==null){
	%>
	<a href="<%=oauth.getAuthUrl()%>">Sign in with Twitter</a>
	
	<%} else {
		
	%>
	
	<%}%> --%>
	<div style="width: 1000px; height: 30px;text-align: center; font-size: 12px;">
		Enter a Twitter user name: 
		<input type="text" id="usernameinput" value="<%=screenName%>"/> 
		<input id="operateBtnId" type="button" onclick="getUserGraph()"
			value="Get" />
		<input id="clearBtnId" type="button" onclick="clearGraph()"
			value="Clear" />
<!-- 	<a href="javascript:void(0)" onclick="logout()">Sign out</a> -->
<!-- 		<div style="float:left;width:100px;" id="sliderId"></div> -->
<!-- 		<div style="float:left;margin-left:20px;width:20px;" id="sliderValueId"></div> -->
		
		
	</div>
	  <div id="loadingDiv" style="width:1000px; height: 30px; text-align: center"></div>
   
    <div id="screen" style="width:1000px; height: 500px"></div>
    <br>
    <div id="progressbar-message"></div>
   <div style="width:998px" id="progressbar"></div>
    <script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
    
     <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
    <script src="http://d3js.org/d3.v3.min.js"></script>
    <script type="text/javascript" src="js/debatree.js"></script>
    <script type="text/javascript" src="js/twitterclient.js"></script>
    <script type="text/javascript" src="js/graph.js"></script>
    <script type="text/javascript" src="js/newgraph.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    
    <script type="text/javascript">
	function logout(){
		del_cookie('access_token');
		location.reload();
	}
	function del_cookie(name)
	{
	    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	}
	graph = new NewGraph("#screen");
	tw = new TwitterClient();
	util = new Util(); 
	var slider;
  $(function() {
// 	  slider = $( "#sliderId" ).slider({max:3,min:2,slide: function( event, ui ) {
// 		  var value = ui.value;
//     	graph.threshold = Math.max(value,2) ;
//     	graph.update();
//     	  $('#sliderValueId').html(graph.threshold);
//     } });
		
		$(function() {
		    $("#progressbar").progressbar({ value: 0 });
		});
  })
    </script>
  </body>
</html>


