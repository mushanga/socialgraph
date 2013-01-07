package com.amazonbird.db.data;

public class Locale {
	String locale;
	String currency;
	
	public Locale(String locale, String currency){
		this.locale = locale;
		this.currency = currency;
	}
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
