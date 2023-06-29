package com.api.actions.autocomplete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Address {

	@SerializedName("city")
	@Expose
	private String city;
	@SerializedName("state")
	@Expose
	private String state;
	@SerializedName("district")
	@Expose
	private String district;
	@SerializedName("postalCode")
	@Expose
	private String postalCode;
	@SerializedName("line")
	@Expose
	private List<String> line = null;
	@SerializedName("telecom")
	@Expose
	private List<Telecom> telecom = null;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public List<String> getLine() {
		return line;
	}

	public void setLine(List<String> line) {
		this.line = line;
	}

	public List<Telecom> getTelecom() {
		return telecom;
	}

	public void setTelecom(List<Telecom> telecom) {
		this.telecom = telecom;
	}

}
