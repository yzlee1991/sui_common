package com.lzy.sui.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.lzy.sui.common.model.ProtocolEntity;

public class SocketUtils {

	private static Map<Socket, BufferedReader> brMap = new HashMap<Socket, BufferedReader>();
	private static Map<Socket, BufferedWriter> bwMap = new HashMap<Socket, BufferedWriter>();

	// 非阻塞发送，目前只适用于服务端这种一个线程对应一个客户端不存在并发的情况
	public static void sendByNoBlock(Socket socket, Object obj) throws IOException {
		if (obj == null) {
			return;
		}
		String json = CommonUtils.gson.toJson(obj);
		BufferedWriter bw = getBufferWriter(socket);
		bw.write(json);
		bw.newLine();
		bw.flush();
	}

	public static void send(Socket socket, Object obj) throws IOException {
		if (obj == null) {
			return;
		}
		String json = CommonUtils.gson.toJson(obj);
		BufferedWriter bw = getBufferWriter(socket);
		synchronized (socket) {
			bw.write(json);
			bw.newLine();
			bw.flush();
		}
	}

	public static ProtocolEntity receive(Socket socket) throws IOException {
		BufferedReader br = getBufferedReader(socket);
		String json = br.readLine();
		ProtocolEntity entity = CommonUtils.gson.fromJson(json, ProtocolEntity.class);
		return entity;
	}

	private static BufferedReader getBufferedReader(Socket socket) throws IOException {
		BufferedReader br = brMap.get(socket);
		if (br == null) {
			synchronized (socket) {
				br = brMap.get(socket);
				if (br != null) {
					return br;
				}
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				brMap.put(socket, br);
			}
		}
		return br;
	}

	private static BufferedWriter getBufferWriter(Socket socket) throws IOException {
		BufferedWriter bw = bwMap.get(socket);
		if (bw == null) {
			synchronized (socket) {
				bw = bwMap.get(socket);
				if (bw != null) {
					return bw;
				}
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bwMap.put(socket, bw);
			}
		}
		return bw;
	}

}
