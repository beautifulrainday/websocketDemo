package com.websocket.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller  //注意这里必须为Controller
public class HelloController {

    @RequestMapping("/hello")
    public String helloHtml() {
        return "index";
    }
}
