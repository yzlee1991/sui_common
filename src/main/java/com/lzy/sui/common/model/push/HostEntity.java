package com.lzy.sui.common.model.push;

import java.io.Serializable;

import com.lzy.sui.common.model.ProtocolEntity;

public class HostEntity implements Serializable{

	private String identityId;

	private ProtocolEntity.Identity identity;

	private String name;

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public ProtocolEntity.Identity getIdentity() {
		return identity;
	}

	public void setIdentity(ProtocolEntity.Identity identity) {
		this.identity = identity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "HostEntity [identityId=" + identityId + ", identity=" + identity + ", name=" + name + "]";
	}

}
