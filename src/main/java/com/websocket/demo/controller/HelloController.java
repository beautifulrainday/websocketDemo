package com.websocket.demo.controller;

import com.websocket.demo.handler.MyHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

@Controller  //注意这里必须为Controller
public class HelloController {

    @GetMapping("/hello")
    public String helloHtml() {
        return "index";
    }

    @GetMapping(value = "sendMsg")
    @ResponseBody
    public String sendMsg() {
        boolean bool = new MyHandler().sendMessageToAllUsers(new TextMessage("手动推送的消息"));
        if(!bool){
            return "fail";
        }
        return "succes";
    }
}
