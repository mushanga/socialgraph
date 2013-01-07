package com.amazonbird.statistics;

public class StatElement implements Comparable {

	private String elementName;
	private long latestValue;
	private long diff;
	private boolean updated;

	public StatElement(String name, long value){
		this.elementName = name;
		this.latestValue = value;
		this.diff = 0;
		updated = true;
	}

	public String getElementName() {
		return elementName;
	}

	public long getValue() {
		return latestValue;
	}

	public long getDiff(){
		return diff;
	}

	public void updateElem(StatElement newElem){
		this.diff = newElem.getValue() - latestValue; 
		this.latestValue = newElem.getValue();
		this.updated = true;
	}

	public boolean isUpdated(){
		return updated;
	}

	public void reset(){
		this.updated = false;
		this.diff = 0;
	}

	@Override
	public int hashCode() {
		return elementName.hashCode();
	}

	@Override
	public boolean equals(Object arg0) {
		try{
			return ((StatElement)arg0).getElementName().equals(elementName);
		}catch(Exception e){
			return super.equals(arg0);
		}
	}

	@Override
	public int compareTo(Object arg0) {
		long long1 = diff;
		long long2 = ((StatElement)arg0).getDiff();
		if(long1>long2){
			return -1;
		}
		if(long1<long2){
			return 1;
		}
		return 0;
	}



}
