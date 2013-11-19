package xml;

import javax.xml.bind.annotation.XmlRootElement;

//<?xml version=“1.0” encoding=“utf-8” ?>
//<Ethernet name=“myEth” description="DC to DC tunnel">
//    <rid>123456</rid>
//    <cid>ngkim</cid>
//    <eid>eth123</eid>
//</Ethernet>

@XmlRootElement (name="Ethernet")
public class RequestInfoEthernet {
	String rid;
	String cid;
	String eid;

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

}
