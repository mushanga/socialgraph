package com.amazonbird.amazonproxy;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AmazonProduct {
	String itemId;
	double price;
	String title;
	public AmazonProduct(String xml){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
	        InputSource is = new InputSource(new StringReader(xml));
	        Document doc = builder.parse(is);
	        XPath xpath = XPathFactory.newInstance().newXPath();
	        XPathExpression expr = xpath.compile("/ItemLookupResponse/Items/Request/ItemLookupRequest/ItemId/text()");
	        itemId = (String)expr.evaluate(doc, XPathConstants.STRING);
	        
	        expr = xpath.compile("/ItemLookupResponse/Items/Item/OfferSummary/LowestNewPrice/Amount/text()");
	        String lowestPriceStr = (String)expr.evaluate(doc, XPathConstants.STRING);
	        if(lowestPriceStr != null){
	        	price = Double.parseDouble(lowestPriceStr) / 100;
	        }
	        else{
	        	expr = xpath.compile("/ItemLookupResponse/Items/Item/ItemAttributes/ListPrice/Amount/text()");
	        	price = Double.parseDouble((String)expr.evaluate(doc, XPathConstants.STRING)) / 100;
	        }
	        
	        expr = xpath.compile("/ItemLookupResponse/Items/Item/ItemAttributes/Title/text()");
	        title = (String)expr.evaluate(doc, XPathConstants.STRING);
	        
	        //System.out.println(result);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
