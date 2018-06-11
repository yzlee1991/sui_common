package com.lzy.sui.common.inf;

import java.util.List;

import com.lzy.sui.common.model.push.HostEntity;

/**
 * author :lzy
 * date   :2018年5月8日下午5:13:32
 */

public interface HostInf extends Rmiable{

	public String a(int a);
	
	public List<HostEntity> getOnlineHostEntity();
	
}
