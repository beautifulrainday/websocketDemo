package com.websocket.demo.handler;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class RoomHandler implements WebSocketHandler {
    //在线用户列表
    private static final Map<String, WebSocketSession> users;
    //房间
    private static final Map<String, Map> roomUsers;

    static {
        users = new HashMap<>();
        roomUsers = new HashMap<>();
    }

    //新增socket
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("成功建立连接");
        String userId = getClientId(session);
        String roomNo = getRoomNo(session);
        if (userId != null && roomNo != null) {
            users.put(userId, session);
            Map userIdandSession = roomUsers.get(roomNo);
            if (null != userIdandSession) {
                userIdandSession.put(userId, session);
                roomUsers.put(roomNo, userIdandSession);
            } else {
                userIdandSession = new HashMap();
                userIdandSession.put(userId, session);
                roomUsers.put(roomNo, userIdandSession);
            }
            session.sendMessage(new TextMessage("成功建立socket连接"));
        }
        System.out.println("当前房间号：" + roomNo + "当前总在线人数：" + users.size() + "当前房间在线人数：" + roomUsers.get(roomNo).size());
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws IOException {
        JSONObject jsonobject = JSONObject.fromObject(webSocketMessage.getPayload());
        /*System.out.println(jsonobject.get("id"));
        System.out.println(jsonobject.get("message") + ":来自" + getClientId(webSocketSession) + "的消息");*/
        String roomNo = getRoomNo(webSocketSession);
        Map<String, WebSocketSession> map = roomUsers.get(roomNo);
        for (Map.Entry<String, WebSocketSession> m : map.entrySet()) {
            sendMessageToUser(m.getKey(),
                    new TextMessage(getRoomNo(webSocketSession) + "房间的人听好了," + jsonobject.get("id") + "说jasiojfsoidjf"));
        }

    }

    /**
     * 发送信息给指定用户
     *
     * @param clientId
     * @param message
     * @return
     */
    public boolean sendMessageToUser(String clientId, TextMessage message) throws IOException {
        if (users.get(clientId) == null) return false;
        WebSocketSession session = users.get(clientId);
        System.out.println("sendMessage:" + clientId);
        if (!session.isOpen()) return false;
        session.sendMessage(message);
        return true;
    }

    /**
     * 广播信息
     *
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

        return allSendSuccess;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("连接出错");
        users.remove(getClientId(session));
        String roomNo = getRoomNo(session);
        Map mapRI = roomUsers.get(roomNo);
        mapRI.remove(getClientId(session));
        if (null == mapRI) {
            System.out.println("当前房间" + roomNo + "在线人数还剩下：0");
        } else {
            System.out.println("当前房间" + roomNo + "在线人数还剩下：" + mapRI.size());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        System.out.println("连接已关闭：" + closeStatus);
        users.remove(getClientId(session));
        String roomNo = getRoomNo(session);
        Map mapRI = roomUsers.get(roomNo);
        mapRI.remove(getClientId(session));
        if (null != mapRI && mapRI.size() == 0) {
            roomUsers.remove(roomNo);
        }
        if (null == mapRI) {
            System.out.println("当前房间" + roomNo + "在线人数还剩下：0");
        } else {
            System.out.println("当前房间" + roomNo + "在线人数还剩下：" + mapRI.size());
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取用户标识
     *
     * @param session
     * @return
     */
    private String getClientId(WebSocketSession session) {
        try {
            String ID = session.getUri().toString().split("userId=")[1];
            return ID;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取房间号
     *
     * @param session
     * @return
     */
    private String getRoomNo(WebSocketSession session) {
        try {
            String roomNo = session.getUri().toString().split("roomNo=")[1].split("/")[0];
            return roomNo;
        } catch (Exception e) {
            return null;
        }
    }
}
