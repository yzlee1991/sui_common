package com.lzy.sui.common.proxy;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.lzy.sui.common.abs.AbstractSocketHandle;
import com.lzy.sui.common.model.ProtocolEntity;
import com.lzy.sui.common.utils.CommonUtils;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class ResponseSocketHandle extends AbstractSocketHandle implements InvocationHandler {

	private String conversationId;

	public ResponseSocketHandle(Socket socket, Object target, String identityId, String targetId,
			ProtocolEntity.Mode mode) {
		super(socket, target, identityId, targetId, mode);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 反射的调用异常需要处理
		Object returnValue = method.invoke(target, args);

		if(ProtocolEntity.Mode.INVOKE==mode){//调用模式
			byte[] bytes = CommonUtils.ObjectToByteArray(returnValue);
			String base64Reply = Base64.encode(bytes);

			// List<String> paramsType = new ArrayList<String>();
			// for (Class<?> type : method.getParameterTypes()) {
			// paramsType.add(type.getTypeName());
			// }

			ProtocolEntity protocol = new ProtocolEntity();
			protocol.setConversationId(conversationId);
			protocol.setType(ProtocolEntity.Type.RESPONSE);
			protocol.setReplyState(ProtocolEntity.ReplyState.SUCCESE);
			// protocol.setMethodName(method.getName());
			// protocol.setParamsType(paramsType);
			protocol.setReply(base64Reply);
			protocol.setIdentityId(identityId);
			protocol.setTargetId(targetId);
			String json = gson.toJson(protocol);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write(json);
			bw.newLine();
			bw.flush();
		}else{
			//命令模式
		}
		
		return null;

	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

}
