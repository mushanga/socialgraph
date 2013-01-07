package com.amazonbird.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.log4j.Logger;


public class StatGroup {
	
	private static Logger logger = Logger.getLogger(StatGroup.class);
	private final String name;
	private ArrayList<StatElement> elements;
	private final String query;
	
	public StatGroup(String name, String query){
		this.name = name;
		this.elements = new ArrayList<StatElement>();
		this.query = query; 
	}
		
	public String getQuery(){
		return query;
	}
	
	public String getName(){
		return name;
	}
	
	
	public void processStatGroup(ResultSet rs) throws SQLException{
		for(StatElement loopElem : elements){
			//reset all in order to remove not updated 
			loopElem.reset();
		}
		while (rs.next()) {
			String name = rs.getString(1);
			long value = rs.getLong(2);
			StatElement tmpElem = new StatElement(name,value);
			int elemIndex = elements.indexOf(tmpElem);
			if(elemIndex > -1){
				//already in the list. Calculate diff
				elements.get(elemIndex).updateElem(tmpElem);
			}else{
				elements.add(tmpElem);
			}
			Collections.sort(elements);
			
		}
		
		
		Iterator<StatElement> it = elements.iterator();
		while(it.hasNext()){
			StatElement loopElem = it.next();
			if(!loopElem.isUpdated()){
				it.remove();
			}
		}
		
	}
	
	public String generateCSV(String date){
		String result = "";
		
		for(StatElement loopElem : elements){
			if(loopElem.isUpdated()){
				result = result+"\n"+date+","+name+","+loopElem.getElementName()+","+loopElem.getValue()+","+loopElem.getDiff();
			}

		}
		return result;
	}
	
	public void logResult(int limit){
		String result;
		int limitIndex = 0;
		for(StatElement loopElem : elements){
			if(loopElem.isUpdated()){
				if(limitIndex==limit){
					break;
				}
				result = ","+name+","+loopElem.getElementName()+","+loopElem.getValue()+","+loopElem.getDiff();
				logger.debug(result);
				System.out.println(result);
				limitIndex++;
			}
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object arg0) {
		try{
			return ((StatGroup)arg0).getName().equals(name);
		}catch(Exception e){
			return super.equals(arg0);
		}
	}
		
}
