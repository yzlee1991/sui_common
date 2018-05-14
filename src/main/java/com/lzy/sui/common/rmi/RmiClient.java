package com.lzy.sui.common.rmi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.lzy.sui.common.model.Conversation;
import com.lzy.sui.common.model.ProtocolEntity;
import com.lzy.sui.common.proxy.RmiRequestSocketHandle;
import com.lzy.sui.common.utils.CommonUtils;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class RmiClient {

	private static Gson gson = new Gson();

	public static Object lookup(Socket socket, String rmiName) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		ProtocolEntity entity = new ProtocolEntity();
		String conversationId = UUID.randomUUID().toString();
		entity.setType(ProtocolEntity.Type.RMI);
		entity.setConversationId(conversationId);
		entity.setIdentityId("1");// 临时
		entity.setRmiName(rmiName);

		String json = gson.toJson(entity);
		bw.write(json);
		bw.newLine();
		bw.flush();

		
		Conversation.Data data = new Conversation.Data();
		String lock = new String(conversationId);
		data.setLock(lock);
		Conversation.MAP.put(conversationId, data);
		synchronized (lock) {
			try {
				lock.wait(Conversation.REQUESTTIMEOUT);
				ProtocolEntity replyEntity = Conversation.MAP.get(conversationId).getEntity();
				Conversation.MAP.remove(conversationId);
				if (replyEntity == null) {
					throw new RuntimeException("rmi服务请求超时");
				}
				if (ProtocolEntity.ReplyState.SUCCESE == replyEntity.getReplyState()) {
					String infName = replyEntity.getReply();
					Class inf = Class.forName(infName);
					RmiRequestSocketHandle rsh = new RmiRequestSocketHandle(socket, rmiName);
					return Proxy.newProxyInstance(inf.getClassLoader(), new Class[] { inf }, rsh);//重点，如何只通过接口获得代理类

				} else if (ProtocolEntity.ReplyState.ERROR == replyEntity.getReplyState()) {
					throw new RuntimeException(replyEntity.getReply());
				} else {
					// 未定义的回复状态
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
