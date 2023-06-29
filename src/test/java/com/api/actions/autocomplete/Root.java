package com.api.actions.autocomplete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;


@Generated("jsonschema2pojo")
public class Root {

	@SerializedName("orgName")
	@Expose
	private String orgName;
	@SerializedName("tin")
	@Expose
	private Object tin;
	@SerializedName("address")
	@Expose
	private List<Address> address = null;

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Object getTin() {
		return tin;
	}

	public void setTin(Object tin) {
		this.tin = tin;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

}
