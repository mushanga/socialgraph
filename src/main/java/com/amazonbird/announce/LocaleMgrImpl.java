package com.amazonbird.announce;

import java.util.HashMap;
import java.util.Map;

import com.amazonbird.db.data.Locale;


public class LocaleMgrImpl {
	Map<String, Locale> locales = new HashMap<String, Locale>();
	private static final LocaleMgrImpl instance = new LocaleMgrImpl();
	public void loadLocales(){
		locales.put("en", new Locale("en", "$"));
		locales.put("tr", new Locale("tr", "TL"));
	}
	
	public static LocaleMgrImpl getInstance() {
		return instance;
	}
	
	public Locale getLocale(String localeCode){
		return locales.get(localeCode);
	}
	
	public String getCurrency(String localeCode){
		return locales.get(localeCode).getCurrency();
	}
}
