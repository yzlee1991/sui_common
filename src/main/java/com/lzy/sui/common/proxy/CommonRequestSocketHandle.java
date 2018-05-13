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
import com.lzy.sui.common.model.Conversation;
import com.lzy.sui.common.model.ProtocolEntity;
import com.lzy.sui.common.utils.CommonUtils;
import com.lzy.sui.common.utils.MillisecondClock;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class CommonRequestSocketHandle extends AbstractSocketHandle implements InvocationHandler {

	// protected long requestTimeout = 10000;

	public CommonRequestSocketHandle(Socket socket, Object target, String identityId, String targetId,
			ProtocolEntity.Mode mode) {
		super(socket, target, identityId, targetId, mode);
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

		ProtocolEntity entity = new ProtocolEntity();
		String conversationId = UUID.randomUUID().toString();
		entity.setConversationId(conversationId);
		entity.setType(ProtocolEntity.Type.COMMONREQUEST);
		entity.setClassName(target.getClass().getName());
		entity.setMethodName(method.getName());
		entity.setParamsType(paramsType);
		entity.setParams(params);
		entity.setIdentityId(identityId);
		entity.setTargetId(targetId);
		entity.setMode(mode);
		String json = gson.toJson(entity);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bw.write(json);
		bw.newLine();
		bw.flush();

		if (ProtocolEntity.Mode.INVOKE == mode) {// 调用模式
			synchronized (conversationId) {
				try {
					conversationId.wait(Conversation.REQUESTTIMEOUT);
					ProtocolEntity replyEntity = Conversation.MAP.get(conversationId);
					Conversation.MAP.remove(conversationId);
					if (replyEntity == null) {
						throw new RuntimeException("请求超时");
					}
					if (ProtocolEntity.ReplyState.SUCCESE == replyEntity.getReplyState()) {
						String base64Reply = replyEntity.getReply();
						byte[] bytes = Base64.decode(base64Reply);
						Object reply = CommonUtils.byteArraytoObject(bytes);
						return reply;
					} else if (ProtocolEntity.ReplyState.ERROR == replyEntity.getReplyState()) {
						throw new RuntimeException(replyEntity.getReply());
					} else {
						// 未定义的回复状态
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			// 命令模式
		}

		return null;
	}

}
