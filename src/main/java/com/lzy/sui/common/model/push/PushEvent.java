package com.lzy.sui.common.model.push;

import java.io.Serializable;

public class PushEvent implements Serializable {

	// 对应推送的json数据
	protected String json;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
