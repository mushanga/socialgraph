package com.amazonbird.amazonproxy;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


public class AmazonResponse {
	public String toString() {
		String result;
		StringWriter sw = new StringWriter();
		try {
			JAXBContext objectContext = JAXBContext.newInstance(this.getClass());
			Marshaller objectMarshaller = objectContext.createMarshaller();
			objectMarshaller.marshal(this, sw);
			result = sw.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

		return result;
	}
}
