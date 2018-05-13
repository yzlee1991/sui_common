package com.lzy.sui.common.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Conversation {

	public static ConcurrentMap<String, ProtocolEntity> MAP = new ConcurrentHashMap<String, ProtocolEntity>();

	public static long REQUESTTIMEOUT = 10000;
	
}
