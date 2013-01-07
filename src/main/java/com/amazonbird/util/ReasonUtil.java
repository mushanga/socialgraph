package com.amazonbird.util;

import com.amazonbird.db.data.Reason;

public class ReasonUtil {
	private static final String defaultReason = "Tweet";
	private static ReasonUtil instance = new ReasonUtil();
	
	private ReasonUtil(){
		
		
	}
	
	public static ReasonUtil getInstance(){
		
		return instance;
	}

	public String getOptionListHtmlForReasonTypes(){
		
		String options = "";
		for(int i=0; i<Reason.reasonTypeIds.length ; i++){
			if(Reason.reasonTypeIds[i] == Reason.TYPE_TWEET){
				options = options + "<option value='"+Reason.reasonTypeIds[i]+"' selected='selected'>"+Reason.reasonTypes[i]+"</option>\n";
			}
			else{
				options = options + "<option value='"+Reason.reasonTypeIds[i]+"'>"+Reason.reasonTypes[i]+"</option>\n";
			}
			
			
		}
		 return options;
		
		
	}
	

}
