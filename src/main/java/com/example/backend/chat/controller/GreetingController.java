package com.example.backend.chat.controller;

import com.example.backend.chat.model.Chat;
import com.example.backend.chat.model.Greeting;
import com.example.backend.chat.model.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;


@RestController
@RequestMapping("/chat")
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(100); // delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Chat chat(Chat chat) throws Exception {
        return new Chat(chat.getName(), chat.getMessage());
    }
}
