package com.example.websocket.controller;

import com.example.websocket.model.GreetingMessage;
import com.example.websocket.model.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingMessage greeting(HelloMessage helloMessage) throws InterruptedException {
        Thread.sleep(1000); // Delay 1 sec
        return new GreetingMessage("Hello, " + HtmlUtils.htmlEscape(helloMessage.name()) + "!");
    }
}
