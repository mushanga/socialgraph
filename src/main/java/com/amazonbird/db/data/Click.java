package com.amazonbird.db.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

public class Click implements DataObjectIF{

	private long id;
	private long announcementId;
	private String srcAddress;
	private String hostname;

	public Click(long announcementId, String srcAddress) {
		this.announcementId = announcementId;
		this.srcAddress = srcAddress;
		this.hostname = "unknown";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAnnouncementId() {
		return announcementId;
	}

	public void setAnnouncementId(long announcementId) {
		this.announcementId = announcementId;
	}

	public String getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(String srcAddress) {
		this.srcAddress = srcAddress;
	}

	public String getHostname(){
		return hostname;
	}
	
	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		this.setId(rs.getLong("id"));
		this.setAnnouncementId(rs.getLong("announcementid"));
		this.setSrcAddress(rs.getString("srcaddress"));

	}

	public void retrieveHostname() throws Exception{
		Record[] answers = new Record[0];
		Resolver res = new ExtendedResolver();
		Name name = ReverseMap.fromAddress(srcAddress);
		int type = Type.PTR;
		int dclass = DClass.IN;
		Record rec = Record.newRecord(name, type, dclass);
		Message query = Message.newQuery(rec);
		Message response = res.send(query);
		answers = response.getSectionArray(Section.ANSWER);
		hostname = answers[0].rdataToString();
	}

	public String toString(){
		String str = "\n"+
				"id="+id+"\n"+
				"announcementid="+announcementId+"\n"+
				"srcaddress="+srcAddress+"\n";
		return str;
	}

}
