package com.api.actions.autocomplete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Telecom {

	@SerializedName("use")
	@Expose
	private String use;
	@SerializedName("value")
	@Expose
	private String value;
	@SerializedName("system")
	@Expose
	private String system;

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

}
