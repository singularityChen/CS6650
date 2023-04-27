package org.example;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

class MessageHandler extends DefaultConsumer {
    private ExecutorService threadPool;

    public MessageHandler(Channel channel, ExecutorService threadPool) {
        super(channel);
        this.threadPool = threadPool;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        threadPool.submit(() -> {
            Gson gson = new Gson();
            Request request = gson.fromJson(message, Request.class);
            //process(request.getSwiper(), request.getSwipee(), request.getLeftorright(),likesTable, dislikesTable, swiperTable);
            System.out.println(" [x] Received '" + message + "'");
        });
    }
}