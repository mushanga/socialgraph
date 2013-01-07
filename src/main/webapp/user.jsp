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

<jsp:include page='/tcommerce/docOpen.jsp'>
	<jsp:param value="t-commerce > Ana Sayfa" name="title" />
</jsp:include>

<script type="text/javascript"src="<%=request.getContextPath()%>/js/jquery.masonry.min.js"></script>
<script type="text/javascript"src="<%=request.getContextPath()%>/js/jquery.imagesloaded.min.js"></script>

<script>
	$("ul#topNav li:nth-child(1)").addClass('active');
</script>

<%
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	MessageMgrImpl messageMgr = MessageMgrImpl.getInstance();
	String userid = request.getParameter("user");
	if(userid==null){
		response.sendRedirect(request.getContextPath());
		return;
	}
	Announcer currentUser = announcerMgr.getAnnouncer(Long.parseLong(userid));
	if(currentUser==null){
		response.sendRedirect(request.getContextPath());
	}
%>

<div>
	<div class="userBanner" >
		<div style="float: left">
			<img style="float:left" width="128" height="128" src="<%=currentUser.getPictureUrl()%>" alt="foto" /> 
		</div>
		<div style="margin-left: 15px; margin-top:5px; float: left;">
			<h1><%=currentUser.getLongName()%></h1>
				<% if(currentUser.getDescription() != null){ %>
				<p class="bio">
					<%=currentUser.getDescription()%>
				</p>
				<% } %>
				<p class="location-url">
					<%
						if (currentUser.getLocation() != null) {
					%>
					<span class="location"><a
						href="http://maps.google.com/maps?q=<%=currentUser.getLocation()%>" target="_blank"><%=currentUser.getLocation()%></a>
					</span>
					<%
						}
					%>
					<%
						if(currentUser.getUrl() != null) {
					%>
					<span class="divider">·</span> <span class="url"> <a
						href="<%=currentUser.getUrl()%>" target="_blank"> <%=currentUser.getUrl()%>
					</a> </span>
					<%
						}
					%>
				</p>
			<%int like2= (int) (Math.random() * 30);%>

		</div>
		<div style="float: right; width: 210px;">
			<div style="float: left; vertical-align: top; border-left: 1px solid #EEE; height: 128px; width: 105px;text-align: center;">
			<h1><%=100-like2%>%</h1>
			<h6>Satıcı Güveni</h6>
			</div>
			<div style="margin-right: 10px; float: right; vertical-align: top; border-left: 1px solid #EEE; height: 128px; width: 90px; text-align: center;">
			<h1><%=(int) (Math.random()*10)%></h1>
			<h6>Toplam Satış</h6>
			</div>
		</div>
		
		<div class="clear"></div>
		
	</div>
</div>
<div  style="margin-top:30px" id = "productContainer">
	<%
		List<Product> products = new ArrayList<Product>();
		if (currentUser == null) {
			products = productMgr.getAllProducts();
		} else {
			products = productMgr.getProductsForAnnouncer(currentUser.getId());
		}
		Util util = Util.getInstance();
		if (util.isListValid(products)) {

			for (Product product : products) {
				Announcer annc = announcerMgr.getAnnouncer(product
						.getAnnouncerId());
				String userName = null;
				String picLink = null;
				if (annc != null) {
					userName = annc.getScreenName();
					picLink = annc.getPictureUrl();
				}
				String[] urls = product.getPictureUrls();
				ProductMessage pMsg = messageMgr
						.getMessageForProduct(product.getId());
				String message = "";
				if (pMsg != null) {
					message = pMsg.getText();
				}
				String url = " ";
				if (urls != null && urls.length > 0) {
					url = urls[0];
				}
	%>
	<div class="productWindow">
		<div class="productCard" onclick="window.location='<%=request.getContextPath()+"/product.jsp?product="+product.getId()%>'" style='cursor: pointer;'>
			<div class="productImg">
				<img src="<%=url%>" alt="foto" />
			</div>

			<div class="productDetail">
				<%=message%>
			</div>
		</div>
		<div class="clear"></div>
		<div class="cardUser" onclick="window.location='<%=request.getContextPath()+"/user.jsp?user="+annc.getId()%>'" style='cursor: pointer;'>
			<img src="<%=picLink%>" alt="foto" />
			<span class="name"><%=userName%></span>
		</div>
	</div>
	<%
		}
		}
	%>

</div>
<div class="clear"></div>

<script type="text/javascript">
var $container = $('#productContainer');
$container.imagesLoaded( function(){
	$container.masonry({
	  itemSelector: '.productWindow',
	  columnWidth: 270
	});
});
</script>

<jsp:include page='/mgr/product/add.jsp' />

<jsp:include page='/tcommerce/docClose.jsp' />