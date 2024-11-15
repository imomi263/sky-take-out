package com.sky.Task;

import com.sky.WebSocket.WebSocketServer;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WebSocketTask {
    @Resource
    private WebSocketServer webSocketServer;

    @Scheduled(cron= "0/5 * * * * ?" )
    public void sendMessageToClient() {
        webSocketServer.sendToAllClient("这是来自服务端的消息：" +
                DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    }
}
