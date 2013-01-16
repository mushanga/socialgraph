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
Announcer ann =  oauth.handle(request,response);
//getServletContext().getRequestDispatcher("/").forward(request, response);

%>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
  </head>
  <body onload="redirect()">

<script>
function redirect(){
	
	window.location.href = '../';
}


</script>
  </body>
</html>


