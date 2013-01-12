<%@page import="com.debatree.twitter.TwitterClient"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.Date"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.FileWriter"%>
<%@page import="com.debatree.task.FindDebateTask"%>
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
	
TwitterClient tc = TwitterClient.getDefaultClient();

%>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
    <link type="text/css" rel="stylesheet" href="style/newgraphstyle.css"/>
    <style type="text/css">



    </style>
  </head>
  <body>
	<h2 style="text-align: center">Twitter Graph</h2>
	<div style="width: 1000px; height: 30px;text-align: center; font-size: 12px;">
		
		Enter a Twitter user name: 
		<input type="text" id="usernameinput" /> 
		<input type="button" onclick="getUserAsRoot(document.getElementById('usernameinput').value)"
			value="Get" />
		<input type="button" onclick="graph.clear()"
			value="Clear" />
		
	</div>
	  <div id="loadingDiv" style="width:1000px; height: 30px; text-align: center"></div>
   
    <div id="screen" style="width:1000px; height: 700px"></div>
   
    <script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
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
    </script>
  </body>
</html>


