
<%@page import="com.tcommerce.search.SearchServlet"%>
<%@page import="org.apache.http.HttpRequest"%>
<%@page import="com.amazonbird.announce.AnnouncerMgrImpl"%>
<%@page import="com.tcommerce.signin.CallBackServlet"%>
<%@page import="com.amazonbird.db.data.Announcer"%>
<%@page import="javax.servlet.http.Cookie" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	isELIgnored="true" session="false"%>

<%

loadUserFromCookie(request);
Announcer announcer = (Announcer)request.getSession().getAttribute(
			Announcer.ANNOUNCER);
	request.setAttribute(Announcer.ANNOUNCER, announcer);
%>

<%!
public void loadUserFromCookie(HttpServletRequest request){
	if (request.getAttribute(Announcer.ANNOUNCER) != null) {
		return;
	}
	Cookie[] cookies = request.getCookies() == null ? new Cookie[] {}: request.getCookies();
	boolean idFound = false;
	boolean oAuthFound = false;
	String idStr = "";
	String oAuth = "";
	Announcer announcer = null;
	for (Cookie cookie : cookies) {
		if (cookie.getName().equals(CallBackServlet.COOKIE_ID)) {
			idStr = cookie.getValue();
			idFound = true;
		}
		if (cookie.getName().equals(CallBackServlet.COOKIE_OAUTHTOKEN)) {
			oAuth = cookie.getValue();
			oAuthFound = true;
		}
		if (idFound && oAuthFound) {
			try {
				long id = Long.parseLong(idStr);
				announcer = AnnouncerMgrImpl.getInstance().getAnnouncer(id);
				if (announcer != null && oAuth.equals(announcer.getAuthToken())) {
					request.setAttribute(Announcer.ANNOUNCER, announcer);
					request.getSession().setAttribute(Announcer.ANNOUNCER, announcer);
					break;
				}
			} catch (NumberFormatException nfe) {
				// log here someday.
			}
			break;
		}
	}
}
%>
<!DOCTYPE html>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title><%=request.getParameter("title")%></title>

<script>
var ctx = '<%=request.getContextPath()%>';
</script>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>

<link
	href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet/less" type="text/css"
	href="<%=request.getContextPath()%>/css/tcommerce.less">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/tcommerce.css" />


<script type="text/javascript">
	google.load("jquery", "1.7.1");
</script>
<script
	src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/less-1.3.0.min.js"></script>
	<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/template.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/tcommerce.js"></script>


</head>

<body background="<%=request.getContextPath()%>/images/paper.jpg">
	<div class="container-fluid">

		<div class="navbar">
			<div class="navbar-inner">

				<!-- Your site name for the upper left corner of the site -->
				<a class="brand" href="<%=request.getContextPath()%>">t-commerce</a>
				<ul class="nav" id="topNav">
					<li><a href="<%=request.getContextPath()%>">Ana Sayfa</a></li>
					<%
						if (request.getSession().getAttribute(Announcer.ANNOUNCER) != null) {
					%>
					<li><a href="#addProductModal" data-toggle="modal" data-target="#addProductModal">Ürün Ekle</a></li>
					<%
						}
					%>
				</ul>
				<form class="navbar-search pull-center" style="margin-left: 15%;" method="get" action="<%=request.getContextPath()%>/search">
				 <div class="input-append">
					<input type="text" class="search-query" placeholder="Ürün Ara" name="query" value="<%=request.getAttribute(SearchServlet.PRODUCT_SEARCH_KEYWORD) == null ? "" : request.getAttribute(SearchServlet.PRODUCT_SEARCH_KEYWORD).toString() %>">
					<span class="add-on"><i class="icon-search"> </i></span>
					</div>
				</form>
				<ul class="nav pull-right">
					<%
						if (announcer == null) {
					%>
					<li><a class="noPad"
						href="<%=request.getContextPath()%>/signin"><img
							title="Sign in with Twitter" alt="Sign in with Twitter"
							src="https://dev.twitter.com/sites/default/files/images_documentation/sign-in-with-twitter-gray.png">
					</a></li>
					<%
						} else {
					%>
					<li class="dropdown"><a class="dropdown-toggle" href="#"
						data-toggle="dropdown"><%=announcer.getScreenName()%> <strong
							class="caret"></strong> </a>
						<ul class="dropdown-menu">
							<li><a href="<%=request.getContextPath()%>/index.jsp?user=<%=announcer.getId()%>">Ürünlerim</a>
							</li>
							<li><a href="<%=request.getContextPath()%>/user.jsp?user=<%=announcer.getId()%>">Profilim</a>
							</li>
							<li><a href="#" onclick="signout('<%=request.getContextPath()%>/signout')">Çıkış</a>
							</li>
						</ul>
					</li>
					<%
						}
					%>
				</ul>


			</div>
		</div>

		<!-- <div class="row-fluid">
			<div class="span1">

			</div>
			<div class="span11">
			-->