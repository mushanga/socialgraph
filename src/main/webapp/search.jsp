<%@page import="com.tcommerce.search.SearchServlet"%>
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

<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jquery.masonry.min.js"></script>

<script>
	$("ul#topNav li:nth-child(1)").addClass('active');
</script>



<%
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	MessageMgrImpl messageMgr = MessageMgrImpl.getInstance();
	Announcer announcer = (Announcer)request.getSession().getAttribute(
	Announcer.ANNOUNCER);
%>

<div id="productContainer">
	<%
		List<Product> products = (List<Product>)request.getAttribute(SearchServlet.PRODUCT_SEARCH_RESULT);
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
			String keyword = (String)request.getAttribute(SearchServlet.PRODUCT_SEARCH_KEYWORD);
			if(keyword != null && keyword.length() > 0){
				message = message.replace(keyword, "<span style='background-color: orange;'><b>" + keyword + "</b></span>");
			}
		}
		String url = " ";
		if (urls != null && urls.length > 0) {
			url = urls[0];
		}
	%>
	<div class="productWindow">
		<div class="productCard"
			onclick="window.location='<%=request.getContextPath()+"/product.jsp?product="+product.getId()%>'"
			style='cursor: pointer;'>
			<div class="productImg">
				<img src="<%=url%>" alt="foto" />
			</div>

			<div class="productDetail">
				<%=message%>
			</div>
		</div>
		<div class="clear"></div>
		<div>
			<p style="text-align: right; margin-right: 10px;">
				<span class="comment"><%=(int)(Math.random() * 100)%> Görüntülenme</span>
				<span class="comment">&#8226;</span> <span class="comment"><%=(int)(Math.random() * 20)%>
					Yorum</span>
			</p>
		</div>
		<div class="clear"></div>
		<div class="cardUser"
			onclick="window.location='<%=request.getContextPath()+"/user.jsp?user="+annc.getId()%>'"
			style='cursor: pointer;'>
			<img src="<%=picLink%>" alt="foto" /> <span class="name"><%=userName%></span>
			<%
				int like = (int)(Math.random() * 30);
			%>
			<div>
				<p
					style="text-align: right; margin-bottom: 1px; padding-bottom: 1px;">
					<span class="comment">Satıcı Güveni <%=100-like%>%</span>
				</p>
			</div>
			<div class="video-extras-sparkbars" style="margin-bottom: 2px;">
				<div class="video-extras-sparkbar-likes" style="width: <%=100-like%>%"></div>
				<div class="video-extras-sparkbar-dislikes"
					style="width: <%=like%>%"></div>
			</div>
			<%
				if(product.getRelation2User() != -1 && product.getRelation2User() != 0){
			%>
			<div>
				<p style="text-align: right;"><span class="comment"><%=product.getRelation2User()%>. derece arkadaş</span></p>
			</div>
			<%
				}
			%>
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
	$container.imagesLoaded(function() {
		$container.masonry({
			itemSelector : '.productWindow',
			columnWidth : 270
		});
	});
</script>
<jsp:include page='/mgr/product/add.jsp' />
<jsp:include page='/tcommerce/docClose.jsp' />