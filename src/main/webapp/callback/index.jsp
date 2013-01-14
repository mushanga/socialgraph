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
	
TwitterClient tc = TwitterClient.getDefaultClient();

OAuth oauth = OAuth.getInstance();
Announcer ann =  oauth.handle(request,response);
//getServletContext().getRequestDispatcher("/").forward(request, response);

%>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
  </head>
  <body onload="setCookie()">

<script>
function setCookie(){
	_setCookie('access_token','<%=ann.getAccessToken()%>',365*24*60*60*1000);
	window.location.href = '../';
}

function _setCookie(c_name,c_value,c_expiredays) {
    var exdate=new Date();
    exdate.setDate(exdate.getDate()+c_expiredays);
    document.cookie=c_name+ "=" +escape(c_value)+
    ((c_expiredays==null) ? "" : ";expires="+exdate.toGMTString());
}

</script>
  </body>
</html>


