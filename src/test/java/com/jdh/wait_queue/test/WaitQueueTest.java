package com.jdh.wait_queue.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class WaitQueueTest {

    final String TARGET_URI = "http://localhost:8080/connect";
    final String SEND_MESSAGE_URI = "/app/queue/123456";
    WebSocketStompClient stompClient;

    private List<Transport> createTransportClient(){
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void contextLoad() throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        StompHeaders stompHeaders = new StompHeaders();
        StompSession stompSession = stompClient.connectAsync(TARGET_URI, httpHeaders, stompHeaders, new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);

        // Send
        stompSession.send(SEND_MESSAGE_URI, "test");
    }

}
