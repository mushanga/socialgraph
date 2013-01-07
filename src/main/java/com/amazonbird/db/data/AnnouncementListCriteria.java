/**
	TwitStreet - Twitter Stock Market Game
    Copyright (C) 2012  Engin Guller (bisanthe@gmail.com), Cagdas Ozek (cagdasozek@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/

package com.amazonbird.db.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.util.Util;

public class AnnouncementListCriteria {

	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	
	Util util = Util.getInstance();
	String orderby = "timemodified-desc";
	String[] orderByValues= {"timesent-desc","timesent-asc","timemodified-desc","timemodified-asc"};
	private ArrayList<String> selectedStatuses = new ArrayList<String>();
	private ArrayList<Long> selectedIds = new ArrayList<Long>();

	private ArrayList<Long> productIds = new ArrayList<Long>();
	private ArrayList<Long> announcerIds = new ArrayList<Long>();
	
	private ArrayList<String> customConditions = new ArrayList<String>();
	
	private int page = 1;
	
	private int recordPerPage = Integer.MAX_VALUE;
	

	public void importFromRequest(HttpServletRequest request){
		
		
		Map parameterMap = request.getParameterMap();
		
		Set<String> parameterKeys = parameterMap.keySet();

		
	
		
		for(String key: parameterKeys  ){
			String value =((String[])parameterMap.get(key))[0];
			if(key.contains("status-")){

				if("on".equalsIgnoreCase(value)){
					getSelectedStatuses().add(convertHtmlStatusToSql(key));
				}
			}
			else if(key.equalsIgnoreCase("orderby")){
				orderby =value; 
			}

			else if(key.equalsIgnoreCase("page")){
				page=Integer.valueOf(value); 
			}


			else if(key.equalsIgnoreCase("announcer")){
				announcerIds.clear();
				try{
					Announcer announcer = announcerMgr.getAnnouncerByScreenName(value);
					
					long announcerId = announcer.getId();

					announcerIds.add(announcerId);
				}catch(Exception ex){
					
				}
			}

		
			
		}
		
		
		
	}
	public String htmlGetStatusCheckboxList(){
		
		String html = "";
		int i = 0;
		String[]statuses = Announcement.STATUSES;
		
		html = html + "<div class='ui-checkbox'>";
		
		for(i=0; i<statuses.length; i++){
		
			
			String checked = "";
			String statusKey = "status-"+i;
			String statusName = convertHtmlStatusToSql(statusKey);
			if(getSelectedStatuses().contains(statusName)){
				checked = "checked='checked'";
			}
			
			html = html+ "<input "+checked+" type='checkbox' name='status-"+i+"' id='status-"+i+"' class='custom'>";
			
			html = html + "<label for='status-"+i+"' data-corners='true' data-shadow='false' " +
						" data-iconshadow='true' data-wrapperels='span' data-icon='checkbox-off' data-theme='c' data-mini='true' " +
						" class='ui-btn ui-mini ui-btn-icon-left ui-corner-top ui-checkbox-on ui-btn-up-c'> " +
						" <span class='ui-btn-inner ui-corner-top'> <span class='ui-btn-text'>	"+statuses[i]+ "</span> </span> </label>";
			
		}
		

		html = html + "</div>";
		return html;
	}
	

	public String htmlOrderByOptions(){
		
		String html = "";
		int i = 0;
		
		html = html + "		<div data-role='fieldcontain'>  <label for='orderby' class='select'>order by</label> <select name='orderby' id='orderby'>";
		
		for(i=0; i<orderByValues.length; i++){
		
			
			String selected = "";
			if(orderby.equalsIgnoreCase(orderByValues[i])){
				selected = "selected='selected'";
			}
				
			html = html+ "<option "+selected+" value='"+orderByValues[i]+"'>"+orderByValues[i]+"</option>\n";
			
		}
		

		html = html + "</select> </div>";
		return html;
	}	


	public boolean sqlHasCondition(){
		return Util.stringIsValid(sqlGetConditions());
	}

	
	public String sqlGetOffsetAndLimit(){
		
		int offset = (page-1) * getRecordPerPage();
		
		
		return  " "+offset+","+getRecordPerPage()+" ";
	}
	
	public String sqlGetConditions(){
		
		String sqlStatuses = "(";
		
		int i = 0;
		for(String status : getSelectedStatuses()){
			
			String or = " or ";
			if(i==0){
				or = "";
			}
			sqlStatuses = sqlStatuses + " "+or+" status='"+status+"' ";
			
			i++;
		}
		sqlStatuses = sqlStatuses + ")";
		sqlStatuses = (sqlStatuses.equalsIgnoreCase("()"))?"":sqlStatuses;
		
		String sqlIds = "(";		
		i = 0;
		for(Long id : selectedIds){
			
			String or = " or ";
			if(i==0){
				or = "";
			}
			sqlIds = sqlIds + " "+or+" id="+id+" ";
			
			i++;
		}
		sqlIds = sqlIds + ")";
		sqlIds = (sqlIds.equalsIgnoreCase("()"))?"":sqlIds;
		

		String sqlproductIds = "(";		
		i = 0;
		for(Long id : productIds){
			
			String or = " or ";
			if(i==0){
				or = "";
			}
			sqlproductIds = sqlproductIds + " "+or+" productid="+id+" ";
			
			i++;
		}
		sqlproductIds = sqlproductIds + ")";
		sqlproductIds = (sqlproductIds.equalsIgnoreCase("()"))?"":sqlproductIds;
		

		String sqlAnnouncerIds = "(";		
		i = 0;
		for(Long id : announcerIds){
			
			String or = " or ";
			if(i==0){
				or = "";
			}
			sqlAnnouncerIds = sqlAnnouncerIds + " "+or+" announcerid="+id+" ";
			
			i++;
		}
		sqlAnnouncerIds = sqlAnnouncerIds + ")";
		sqlAnnouncerIds = (sqlAnnouncerIds.equalsIgnoreCase("()"))?"":sqlAnnouncerIds;
		
		
		
		String sqlCustomConditions = "(";
		
		i = 0;
		for(String str: customConditions){
			
			String and = " and ";
			if(i==0){
				and = "";
			}
			sqlCustomConditions = sqlCustomConditions + " "+and+" "+str+" ";
			
			i++;
		}
		sqlCustomConditions = sqlCustomConditions + ")";
		sqlCustomConditions = (sqlCustomConditions.equalsIgnoreCase("()"))?"":sqlCustomConditions;
		

		
		
		
		String sql = "";
		if (Util.stringIsValid(sqlStatuses) && Util.stringIsValid(sqlIds)) {

			sql = sqlStatuses + " and " + sqlIds;

		} else {
			sql = sqlStatuses + sqlIds;
		}
		if (Util.stringIsValid(sql) && Util.stringIsValid(sqlproductIds)) {

			sql = sql + " and " + sqlproductIds;

		} else {
			sql = sql + sqlproductIds;
		}

		if (Util.stringIsValid(sql) && Util.stringIsValid(sqlAnnouncerIds)) {

			sql = sql + " and " + sqlAnnouncerIds;

		} else {
			sql = sql + sqlAnnouncerIds;
		}

		if (Util.stringIsValid(sql) && Util.stringIsValid(sqlCustomConditions)) {

			sql = sql + " and " + sqlCustomConditions;

		} else {
			sql = sql + sqlCustomConditions;
		}

		return sql;
	}	
	public String sqlGetOrderBy(){
		
		String sql = "";
		
		sql = orderby.replace("-", " ");
		
		return " "+ sql +" ";
	}
	
	
	private String convertHtmlStatusToSql(String htmlStatus){
		
		String statusNo = htmlStatus.split("-")[1];
		return Announcement.STATUSES[Integer.valueOf(statusNo)];
		
		
	}
	public ArrayList<Long> getSelectedIds() {
		return selectedIds;
	}
	public void setSelectedIds(ArrayList<Long> selectedIds) {
		this.selectedIds = selectedIds;
	}
	
	public int getLimit() {
		return getRecordPerPage();
	}
	public void setLimit(int limit) {
		this.setRecordPerPage(limit);
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		
			
		this.page = page;
	}
	public int getRecordPerPage() {
		return recordPerPage;
	}
	public void setRecordPerPage(int recordPerPage) {
		this.recordPerPage = recordPerPage;
	}
	/**
	 * @return the productIds
	 */
	public ArrayList<Long> getProductIds() {
		return productIds;
	}
	/**
	 * @param productIds the productIds to set
	 */
	public void setProductIds(ArrayList<Long> productIds) {
		this.productIds = productIds;
	}
	/**
	 * @return the selectedStatuses
	 */
	public ArrayList<String> getSelectedStatuses() {
		return selectedStatuses;
	}
	/**
	 * @param selectedStatuses the selectedStatuses to set
	 */
	public void setSelectedStatuses(ArrayList<String> selectedStatuses) {
		this.selectedStatuses = selectedStatuses;
	}
	/**
	 * @return the customConditions
	 */
	public ArrayList<String> getCustomConditions() {
		return customConditions;
	}
	/**
	 * @param customConditions the customConditions to set
	 */
	public void setCustomConditions(ArrayList<String> customConditions) {
		this.customConditions = customConditions;
	}
	/**
	 * @return the announcerIds
	 */
	public ArrayList<Long> getAnnouncerIds() {
		return announcerIds;
	}
	/**
	 * @param announcerIds the announcerIds to set
	 */
	public void setAnnouncerIds(ArrayList<Long> announcerIds) {
		this.announcerIds = announcerIds;
	}

}