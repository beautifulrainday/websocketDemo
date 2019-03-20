package com.websocket.demo.handler;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 相当于controller的处理器
 */
@Service
public class MyHandler implements WebSocketHandler {
    //在线用户列表
    private static final Map<String, WebSocketSession> users;

    static {
        users = new HashMap<>();
    }

    //新增socket
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("成功建立连接");
        String ID = session.getUri().toString().split("ID=")[1];
        System.out.println(ID);
        if (ID != null) {
            users.put(ID, session);
            session.sendMessage(new TextMessage("成功建立socket连接"));
            System.out.println(ID);
            System.out.println(session);
        }
        System.out.println("当前在线人数："+users.size());
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        JSONObject jsonobject = JSONObject.fromObject(webSocketMessage.getPayload());
        System.out.println(jsonobject.get("id"));
        System.out.println(jsonobject.get("message") + ":来自" + getClientId(webSocketSession) + "的消息");
        sendMessageToUser(jsonobject.get("id")+"",new TextMessage("服务器收到了，hello!"));
        sendMessageToAllUsers(new TextMessage("这是一条给所有用户的公告"));
    }

    /**
     * 发送信息给指定用户
     * @param clientId
     * @param message
     * @return
     */
    public boolean sendMessageToUser(String clientId, TextMessage message) {
        if (users.get(clientId) == null) return false;
        WebSocketSession session = users.get(clientId);
        System.out.println("sendMessage:" + session);
        if (!session.isOpen()) return false;
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 广播信息
     * @param message
     * @return
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> clientIds = users.keySet();
        WebSocketSession session = null;
        for (String clientId : clientIds) {
            try {
                session = users.get(clientId);
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                allSendSuccess = false;
            }
        }

        return  allSendSuccess;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("连接出错");
        users.remove(getClientId(session));
        System.out.println("当前在线人数还剩下："+users.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        System.out.println("连接已关闭：" + closeStatus);
        users.remove(getClientId(webSocketSession));
        System.out.println("当前在线人数还剩下："+users.size());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取用户标识
     * @param session
     * @return
     */
    private String getClientId(WebSocketSession session) {
        try {
            //Integer clientId = (Integer) session.getAttributes().get("WEBSOCKET_USERID");
            String ID = session.getUri().toString().split("ID=")[1];
            return ID;
        } catch (Exception e) {
            return null;
        }
    }
}