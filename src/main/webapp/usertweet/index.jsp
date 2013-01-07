
<%@page import="com.debatree.task.FindDebateTask"%>
<%
	String userName = request.getParameter("user");
	FindDebateTask fdt = FindDebateTask.getInstance();
	String s = fdt.getTweetTreeAsJSON(userName);
%>
<%=s %>

