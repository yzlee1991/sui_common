package com.lzy.sui.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.lzy.sui.common.model.ProtocolEntity;

public class SocketUtils {

	public static void send(Socket socket,Object obj) throws IOException{
		if(obj==null){
			return;
		}
		String json=CommonUtils.gson.toJson(obj);
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		synchronized (socket) {
			bw.write(json);
			bw.newLine();
			bw.flush();
		}
		return;
	}
	
//	public static ProtocolEntity receive(Socket socket) throws IOException{
//		BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		String json=br.readLine();
//		ProtocolEntity entity=CommonUtils.gson.fromJson(json, ProtocolEntity.class);
//		return entity;
//	}
	
}
