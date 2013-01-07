package com.amazonbird.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.amazonbird.amazonproxy.AmazonProduct;
import com.amazonbird.amazonproxy.AmazonProxy;
import com.amazonbird.amazonproxy.AmazonProxyImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.db.data.Product;

public class ProductResource {
	@Context
	UriInfo uriInfo;
	@Context
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	@Context
	AmazonProxy amazonProxy = AmazonProxyImpl.getInstance();
	Request request;
	String id;

	public ProductResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	// Application integration
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Product getProduct() {
		Product product = productMgr.getProductById(id);
		if (product == null)
			throw new RuntimeException("Get: Product with " + id + " not found");
		return product;
	}

	// For the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public Product getProductHTML() {
		Product product = productMgr.getProductById(id);
		if (product == null)
			throw new RuntimeException("Get: Product with " + id + " not found");
		return product;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putProduct(JAXBElement<Product> product) {
		Product c = product.getValue();
		return putAndGetResponse(c);
	}

	@DELETE
	public void deleteProduct() {
		productMgr.removeProduct(Long.valueOf(id));

	}
	
	private Response putAndGetResponse(Product product) {
		Response res;

		res = Response.created(uriInfo.getAbsolutePath()).build();

		productMgr.addProduct(product);
		return res;
	}

}
