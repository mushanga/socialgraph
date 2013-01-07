package com.amazonbird.amazonproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

public class AmazonProxyImpl implements AmazonProxy {
    private static final AmazonProxyImpl instance = new AmazonProxyImpl();
	private static final String ASSOCIATE_TAG = "gujum-20";

	private static final String ENDPOINT = "webservices.amazon.com";
	private static final String AWS_ACCESS_KEY_ID = "AKIAJJV7TTD5QULJUSKQ";
	private static final String AWS_SECRET_KEY = "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";

	SignedRequestsHelper helper;

	public AmazonProxyImpl() {
		try {
			helper = SignedRequestsHelper.getInstance(ENDPOINT,
					AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

    public static AmazonProxyImpl getInstance(){
        return instance;
    }

	@Override
	public AmazonProduct getProduct(String itemId) {
		String xmlBody =  itemLookup(itemId);
		return new AmazonProduct(xmlBody);
	}

	private String itemLookup(String itemId) {
		return itemLookup(itemId, new String[]{ItemLookupResponseGroup.Large});
	}

	private String itemLookup(String itemId,
			String[] responseGroup) {
		return itemLookup(itemId, commaSep(responseGroup));
	}

	private String itemLookup(String itemId,
			String responseGroup) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("Service", "AWSECommerceService");
		params.put("Version", "2011-08-01");
		params.put("Operation", "ItemLookup");
		params.put("ItemId", itemId);
		params.put("ResponseGroup", responseGroup);
		params.put("AssociateTag", ASSOCIATE_TAG);
        /*
		String queryString = "Service=AWSECommerceService&Version=2011-08-01&Operation=ItemLookup&ResponseGroup="
				+ responseGroup
                + "&AWSAccessKeyId="
                + AWS_ACCESS_KEY_ID
				+ "&AssociateTag="
				+ ASSOCIATE_TAG
				+ "&ItemId=" + itemId;
	    */
		String requestUrl = helper.sign(params);
		return  parseXml(requestUrl);
	}

	public static void main(String[] args) {
		AmazonProxyImpl amazonProxyImpl = new AmazonProxyImpl();
		AmazonProduct amazonProduct = amazonProxyImpl.getProduct("B008809VFO");
        System.out.println(amazonProduct.getPrice());
		//amazonProxyImpl.itemLookupResponse("B000W8UF9G");
		// BrowseNodeLookupResponse browseNodeLookupResponse =
		// amazonProxyImpl.browseNodeLookupMostWishedFor("599826");
		// browseNodeLookupResponse =
		// amazonProxyImpl.browseNodeLookupByNodeInfo("599826");
		// browseNodeLookupResponse =
		// amazonProxyImpl.browseNodeLookupByNewReleases("599826");
		// browseNodeLookupResponse =
		// amazonProxyImpl.browseNodeLookupByTopSellers("599826");
		// System.out.println(browseNodeLookupResponse.getBrowseNodes().getBrowseNode().getTopItemSet().getTopItem());
	}

	private String parseXml(String requestUrl) {
		try {
			System.out.println(requestUrl);
			URL url = new URL(requestUrl);
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			return body;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String commaSep(String[] strArr) {
		String str = "";
		if (strArr == null || strArr.length == 0) {
			return str;
		}

		for (int i = 0; i < strArr.length; i++) {
			str += strArr[0];
			if (i < strArr.length - 1) {
				str += ", ";
			}
		}
		return str;
	}

}
