package com.jdh.wait_queue.handler.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SocketHandler {

    @MessageMapping("/queue/{key}/join")
    @SendTo("/topic/{key}")
    public String joinQueue(@Payload String message) {
        log.info("[SocketHandler] (joinQueue) message: {}", message);
        return "message :: " + message;
    }

}
