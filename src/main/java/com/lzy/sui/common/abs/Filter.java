package com.lzy.sui.common.abs;

import java.net.Socket;
import java.util.concurrent.ConcurrentMap;

import com.lzy.sui.common.model.ProtocolEntity;

public abstract class Filter {

	protected Filter filter;

	protected Socket socket;

	public void register(Filter filter) {
		this.filter = filter;
	}

	public abstract void handle(ProtocolEntity entity);


	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
