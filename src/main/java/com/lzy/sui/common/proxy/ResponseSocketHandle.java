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
		if (ProtocolEntity.Mode.INVOKE == mode) {// 调用模式
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String json="";
			try {
				Object returnValue = method.invoke(target, args);
				byte[] bytes = CommonUtils.ObjectToByteArray(returnValue);
				String base64Reply = Base64.encode(bytes);

				ProtocolEntity entity = new ProtocolEntity();
				entity.setConversationId(conversationId);
				entity.setType(ProtocolEntity.Type.RESPONSE);
				entity.setReplyState(ProtocolEntity.ReplyState.SUCCESE);
				entity.setReply(base64Reply);
				entity.setIdentityId(identityId);
				entity.setTargetId(targetId);
				json = gson.toJson(entity);

			} catch (Exception e) {
				System.out.println("捕获到调用异常");
				e.printStackTrace();
				ProtocolEntity entity = new ProtocolEntity();
				entity.setConversationId(conversationId);
				entity.setType(ProtocolEntity.Type.RESPONSE);
				entity.setReplyState(ProtocolEntity.ReplyState.ERROR);
				entity.setReply(e.getMessage());//先简单处理，之后输出整个异常栈信息
				entity.setIdentityId(identityId);
				entity.setTargetId(targetId);
				json = gson.toJson(entity);
			}
			bw.write(json);
			bw.newLine();
			bw.flush();
			
		} else {// 命令模式
			method.invoke(target, args);
		}

		return null;

	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

}
