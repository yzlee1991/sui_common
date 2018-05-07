package com.lzy.sui.common.inf;

import com.lzy.sui.common.model.ProtocolEntity;

public interface Observerable {

	//注册
	public void register(Listener listener);
	
	//注销
	public void remove(Listener listener);
	
	//通知观察者
	public void notifyListener(ProtocolEntity entity);

}
