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
<%@page import="com.amazonbird.db.data.Comment"%>

<jsp:include page='/tcommerce/docOpen.jsp'>
	<jsp:param value="t-commerce > Ana Sayfa" name="title" />
</jsp:include>


<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jquery.masonry.min.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jquery.imagesloaded.min.js"></script>


<script>
	$("ul#topNav li:nth-child(1)").addClass('active');
</script>

<%
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	MessageMgrImpl messageMgr = MessageMgrImpl.getInstance();
	Announcer announcer = (Announcer) request.getSession()
	.getAttribute(Announcer.ANNOUNCER);
	String productStr = request.getParameter("product");
	long productId = Long.parseLong(productStr);
	Product product = productMgr.getProductById(productId);
	ProductMessage productMessage = messageMgr
	.getMessageForProduct(productId);
	Announcer productAnnouncer = announcerMgr.getAnnouncer(product
	.getAnnouncerId());
%>

<div id="productContainer">

	<div class="aloneProductWindow">
		<div  onclick="window.location='<%=request.getContextPath() + "/user.jsp?user="+ productAnnouncer.getId()%>'" style="cursor: pointer;margin-top: 10px; margin-left: 20px; float: left">
			<img src="<%=productAnnouncer.getPictureUrl()%>" alt="foto" /> <span class="name"><%=productAnnouncer.getLongName()%></span>
			<%int like2= (int) (Math.random() * 30);%>
			<p
				style=" margin-bottom: 1px; padding-bottom: 1px;">
				<span class="comment">Satıcı Güveni <%=100 - like2%>%</span>
			</p>
			<div class="video-extras-sparkbars" style="margin-bottom: 2px;">
				<div class="video-extras-sparkbar-likes" style="width: <%=100 - like2%>%"></div>
				<div class="video-extras-sparkbar-dislikes"
					style="width: <%=like2%>%"></div>
			</div>
		</div>
		<div style="margin-top: 10px; margin-right: 20px; float: right">
			<button class="btn btn-danger" type="submit" data-theme="a"
				class="ui-btn-hidden">Satın Al</button>
		</div>
		<div class="clear"></div>
		<div class="aloneProductCard"
			onclick="window.location=<%=request.getContextPath() + "/product.jsp?product="
					+ product.getId()%>"
			style='cursor: pointer;'>
			<div class="productImg">
				<img src="<%=product.getPictureUrls()[0]%>" alt="foto" />
			</div>

			<div class="productDetail">
				<p style="margin-left: 10px; margin-top: 5px;"><%=productMessage.getText()%></p>
			</div>
		</div>
		<div class="clear"></div>
		<div style="margin-left: 20px;">
		<%
		List<Announcer> clickedAnnouncers = productMgr.getAnnouncerWhoViewedProduct(product.getId());
		for(Announcer clickedAnnouncer : clickedAnnouncers){
		%>
		<div style="float: left;">
		<img src="<%=clickedAnnouncer.getPictureUrl()%>" alt="foto" />
		</div>
		<%
		}
		%>
		</div>
		<div class="clear"></div>
		<div>
			<p style="text-align: right; margin-right: 10px;">
				<span class="comment"><%=(int) (Math.random() * 100)%> Görüntülenme</span> 
				<span class="comment">&#8226;</span> 
				<span class="comment"><%=(int) (Math.random() * 20)%> Yorum</span>
			</p>
		</div>
		<div class="clear"></div>
		<div style="margin-left: 10px; margin-top: 10px; margin-bottom: 5px;"
			id="commentContainer">
			<%
				List<Comment> commentList = announcerMgr.getComments(product.getId());
					for(Comment comment : commentList){ 
						Announcer commentOwner = announcerMgr.getAnnouncer(comment.getAnnouncerId());
			%>
			<div style="margin-bottom: 10px;">
				<div style="float: left;">
					<img alt="<%=commentOwner.getScreenName()%>"
						src="<%=commentOwner.getPictureUrl()%>">
				</div>
				<div style="margin-left: 55px;">
					<p style="margin-top: 3px; margin-bottom: 3px;">
						<a
							href="<%=request.getContextPath()%>/user.jsp?user=<%=commentOwner.getId()%>"><b><%=commentOwner.getLongName()%></b>
						</a>
					</p>
					<p><%=comment.getComment()%></p>
				</div>
			</div>
			<%
				}
			%>
		</div>
		<%
			if (request.getSession().getAttribute(Announcer.ANNOUNCER) != null) {
		%>
		<div style="margin-left: 10px; margin-top: 10px">
			<fieldset>
				<div data-role="fieldcontain" class="ui-field-contain ui-body ui-br">
					<label for="message">Yorum</label>
					<textarea name="message" id="message" rows="5" cols="100"
						style="width: 90%"></textarea>
				</div>
				<div data-role="fieldcontain" class="ui-field-contain ui-body ui-br">
					<button class="btn btn-info" type="button" data-theme="a"
						class="ui-btn-hidden" id="add-comment-btn" onclick="addComment();">Yorumu
						Kaydet</button>
				</div>
			</fieldset>
			<input type="hidden" name="announcerid" id="comment-announcer-id"
				value="<%=request.getAttribute(Announcer.ANNOUNCER) == null
						? ""
						: ((Announcer) request
								.getAttribute(Announcer.ANNOUNCER)).getId()%>">
			<input type="hidden" name="productid" id="comment-product-id"
				value="<%=product.getId()%>">
		</div>
		<%
			}
		%>
	</div>
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
<script id="commentTemplate" type="text/html">
			<div style="margin-bottom: 10px;">
				<div style="float: left;">
					<img alt="<#=comment.screenName#>" src="<#=comment.pictureUrl#>">
				</div>
				<div style="margin-left: 55px;">
					<p style="margin-top: 3px; margin-bottom: 3px;"><a href="<#=ctx#>/user.jsp?user=<#=comment.announcerId#>"><b><#=comment.longName#></b></a></p>
					<p><#=comment.comment#></p>
				</div>
			</div>
	</script>
<jsp:include page='/mgr/product/add.jsp' />
<jsp:include page='/tcommerce/docClose.jsp' />