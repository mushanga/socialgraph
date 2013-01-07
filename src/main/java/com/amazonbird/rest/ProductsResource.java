package com.amazonbird.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.amazonbird.amazonproxy.AmazonProduct;
import com.amazonbird.amazonproxy.AmazonProxy;
import com.amazonbird.amazonproxy.AmazonProxyImpl;
import com.amazonbird.announce.MessageMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.config.PropsConfigMgrImpl;
import com.amazonbird.db.data.Product;
import com.amazonbird.db.data.Reason;
import com.amazonbird.servlet.FileUtil;
import com.amazonbird.util.Util;
import com.sun.jersey.multipart.FormDataParam;
import com.tcommerce.util.FileUtils;

@Path("/products")
public class ProductsResource {
	private static Logger logger = Logger.getLogger(ProductsResource.class);

	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	AmazonProxy amazonProxy = AmazonProxyImpl.getInstance();
	MessageMgrImpl msgMgr = MessageMgrImpl.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	Util util = Util.getInstance();

	// Return the list of products
	@GET
	@Produces({ MediaType.TEXT_XML })
	public List<Product> getProducts(@QueryParam("announcer") String announcerId) {
		List<Product> products = new ArrayList<Product>();
		
		long annrId = -1;
		
		try{
			annrId = Long.valueOf(announcerId);
		}catch(Exception ex) {
			
		}
		if(annrId>=0){

			products.addAll(productMgr.getProductsForAnnouncer(annrId));
		}else{

			products.addAll(productMgr.getAllProducts());
		}
		return products;
	}

	// retuns the number of products
	// Use http://localhost:8080/amazonbird/products/count
	// to get the total number of records
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = productMgr.getAllProducts().size();
		return String.valueOf(count);
	}

	// For the browser
	@GET
	@Path("amazon")
	@Produces(MediaType.APPLICATION_JSON)
	public Product getAmazonProduct(@QueryParam("amazonId") String amazonId) {
		AmazonProduct amazonProduct = amazonProxy.getProduct(amazonId);
		if (amazonProduct == null) {
			throw new RuntimeException("Get: Amazon product with " + amazonId + " not found");
		}
		return new Product(amazonProduct);
	}
	

//	 @POST
//	 @Produces(MediaType.TEXT_HTML)
//	 @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	 public void updateProduct(@FormParam("id") String
//	 id,@FormParam("announcerid") String announcerId, @FormParam("name")
//	 String name, @FormParam("dateadded") String dateadded,
//	 @FormParam("price") String price, @FormParam("destination") String
//	 destionation, @FormParam("alternativeDestination") String
//	 alternativeDestination, @FormParam("reasontype") String reasonType,
//	 @FormParam("reasonvalue") String reasonValue, @FormParam("message")
//	 String message, @FormParam("locale") String locale, @Context
//	 HttpServletResponse servletResponse) throws IOException {
//		 
//		 Product product = new Product(Long.valueOf(id), name, -1,
//		 Double.valueOf(price), destionation, alternativeDestination, locale,
//		 Long.valueOf(announcerId));
//		 productMgr.updateProduct(product);
//		 Reason reason = new Reason();
//		 reason.setType(Integer.valueOf(reasonType));
//		 reason.setValue(reasonValue);
//		
//		 reasonMgr.setReasonForProduct(reason, Long.valueOf(id));
//		
//		 msgMgr.setMessageForProduct(product.getId(),message);
//		
//		 servletResponse.sendRedirect(util.getContextPath() +
//		 "/mgr/product/details?id=" + product.getId());
//	 }

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void newProduct(@FormDataParam("id") String id, @FormDataParam("image") File file, @FormDataParam("announcerid") String announcerId, @FormDataParam("name") String name, @FormDataParam("dateadded") String dateadded, @FormDataParam("price") String price, @FormDataParam("destination") String destionation, @FormDataParam("alternativeDestination") String alternativeDestination, @FormDataParam("reasontype") String reasonType, @FormDataParam("reasonvalue") String reasonValue, @FormDataParam("message") String message, @FormDataParam("locale") String locale, @Context HttpServletResponse servletResponse) throws Exception {
		long annrIdLong = -1;
		try {
			annrIdLong = Long.valueOf(announcerId);
		} catch (Exception ex) {

		}
		
		Product product = new Product(-1, name, -1, Double.valueOf(price), "destionation", "alternativeDestination", "en", annrIdLong);

		
		logger.info("Product will be added: "+product);
		product = productMgr.addProduct(product);
		logger.info("Product has been added: "+product);

		if(product.getId()<0){
			throw new Exception("Add Product Error");
		}
		Reason reason = new Reason();
		reason.setType(Reason.TYPE_TWEET);
		reason.setValue(reasonValue);

		reasonMgr.setReasonForProduct(reason, product.getId());

		msgMgr.setMessageForProduct(product.getId(), message);

		String fileName = product.getId() + "_" + new Date().getTime() + ".png";

		String filePath = FileUtil.getInstance().getFilePath();

		//boolean success = file.renameTo(new File(filePath fileName));
		File destFile = new File(filePath + fileName);
		FileUtils.moveFile(file, new File(filePath + fileName));

		//boolean success = move(file, new File(filePath+fileName));

		if (destFile.exists()) {
			productMgr.addProductPicture(product.getId(),  fileName);

		}else{
			String tempFileName = " -none- ";
			try{
				tempFileName = file.getAbsolutePath();
			}catch(Exception ex){
				
			}
			logger.error("Cannot add image: "+filePath+", "+fileName+" temp_file_name: "+tempFileName);
		}

		servletResponse.sendRedirect(util.getContextPath());

	}
	// Defines that the next path parameter after products is
	// treated as a parameter and passed to the ProductResources
	// Allows to type http://localhost:8080/amazonbird/products/1
	// 1 will be treaded as parameter product and passed to ProductResource
	@Path("{productId}")
	public ProductResource getProduct(@PathParam("productId") String id) {
		return new ProductResource(uriInfo, request, id);
	}	
	

}
