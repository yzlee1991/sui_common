package com.lzy.sui.common.infimpl;

import java.util.ArrayList;
import java.util.List;

import com.lzy.sui.common.inf.Listener;
import com.lzy.sui.common.inf.Observerable;
import com.lzy.sui.common.model.ProtocolEntity;

public class Observer implements Observerable{

	private List<Listener> list=new ArrayList<Listener>();
	
	@Override
	public void register(Listener listener) {
		list.add(listener);
	}

	@Override
	public void remove(Listener listener) {
		list.remove(listener);
	}

	@Override
	public void notifyListener(ProtocolEntity entity) {
		for(Listener listener:list){
			listener.action(entity);
		}
	}

}
