package com.lzy.sui.common.proxy;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.lzy.sui.common.abs.AbstractSocketHandle;
import com.lzy.sui.common.model.ProtocolEntity;
import com.lzy.sui.common.utils.CommonUtils;
import com.lzy.sui.common.utils.MillisecondClock;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class RequestSocketHandle extends AbstractSocketHandle implements InvocationHandler {

	protected long requestTimeout = 10000;

	public RequestSocketHandle(Socket socket, Object target, String identityId, String targetId) {
		super(socket, target, identityId, targetId);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?> returnType = method.getReturnType();
		if (!baseType.contains(returnType) && !Serializable.class.isAssignableFrom(returnType)) {
			// 返回值类型不可序列化，抛异常
			System.out.println("返回值不可传输");
			return null;
		}

		List<String> paramsType = new ArrayList<String>();
		List<String> params = new ArrayList<String>();
		Class<?>[] types = method.getParameterTypes();
		for (int i = 0; i < types.length; i++) {

			if (!baseType.contains(types[i]) && !Serializable.class.isAssignableFrom(types[i])) {
				// 参数类型不可序列化，抛异常
				System.out.println("参数不可传输");
				return null;
			}
			byte[] bytes = CommonUtils.ObjectToByteArray(args[i]);
			String base64Param = Base64.encode(bytes);
			params.add(base64Param);
			paramsType.add(types[i].getTypeName());

		}

		ProtocolEntity protocol = new ProtocolEntity();
		String conversationId = UUID.randomUUID().toString();
		protocol.setConversationId(conversationId);
		protocol.setType(ProtocolEntity.Type.REQUEST);
		protocol.setClassName(target.getClass().getName());
		protocol.setMethodName(method.getName());
		protocol.setParamsType(paramsType);
		protocol.setParams(params);
		protocol.setIdentityId(identityId);
		protocol.setTargetId(targetId);
		String json = gson.toJson(protocol);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bw.write(json);
		bw.newLine();
		bw.flush();

		synchronized (conversationId) {
			try {
				conversationId.wait(requestTimeout);
				Object reply=conversationMap.get(conversationId);
				if(reply==null){
					throw new RuntimeException("请求超时");
				}
				return reply;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			conversationMap.remove(conversationId);
		}

		return null;
	}

}
