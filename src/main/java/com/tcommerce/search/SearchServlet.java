package com.tcommerce.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.db.data.Product;
import com.tcommerce.graph.GraphDatabase;

public class SearchServlet extends HttpServlet {
	public static final String PRODUCT_SEARCH_RESULT = "product_search_result";
	public static final String PRODUCT_SEARCH_KEYWORD = "product_search_keyword";
	ProductMgrImpl productMgrImpl = ProductMgrImpl.getInstance();
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		List<Product> productList = productMgrImpl.getProductsByKeyWord(query);
		Announcer announcer = (Announcer) request.getSession().getAttribute(Announcer.ANNOUNCER);
		if(announcer != null){
			productList = sortProductsForAnnouncer(announcer, productList);
		}
		request.setAttribute(PRODUCT_SEARCH_RESULT, productList);
		request.setAttribute(PRODUCT_SEARCH_KEYWORD, query);
		getServletContext().getRequestDispatcher("/search.jsp").forward(request, response);
		System.err.println(query);
	}
	private List<Product> sortProductsForAnnouncer(Announcer announcer,
			List<Product> productList) {
		List<Product> sortedProductList = new ArrayList<Product>();
		for(Product product : productList){
			int relation = GraphDatabase.findConnectionBetweet(announcer.getId(), product.getAnnouncerId());
			product.setRelation2User(relation);
			sortedProductList.add(product);
		}
		Collections.sort(sortedProductList, new RelationComparator());
		return sortedProductList;
	}
	
	class RelationComparator implements Comparator<Product> {

		@Override
		public int compare(Product o1, Product o2) {
			if(o1.getRelation2User() == -1 ){
				return -1;
			}
			if(o1.getRelation2User() == o2.getRelation2User()){
				return 0;
			}
			return o1.getRelation2User() > o2.getRelation2User() ? 1 : -1;
		}
		
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

}
