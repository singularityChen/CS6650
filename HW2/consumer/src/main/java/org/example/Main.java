package org.example;
import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Main {

    private final static String QUEUE_NAME = "my-queue";
    private static Hashtable<String, Integer> likesTable = new Hashtable<>();
    private static Hashtable<String, Integer> dislikesTable = new Hashtable<>();
    private static Hashtable<String, List<String>> swiperTable = new Hashtable<>();

    private static int THREAD_NUMBER = 200;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("35.85.50.119");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // create a thread pool with 10 threads
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUMBER);


        // create a consumer object
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                // submit a new task to the thread pool for each message received
                threadPool.submit(() -> {
                    Gson gson = new Gson();
                    Request request = gson.fromJson(message, Request.class);
                    process(request.getSwiper(), request.getSwipee(), request.getLeftorright(),likesTable, dislikesTable, swiperTable);
                    System.out.println(" [x] Received '" + message + "'");
                });

            }
        };

        // start consuming messages
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    public static void process(String swiper, String swipee, String leftorright, Hashtable<String, Integer> likesTable, Hashtable<String, Integer> dislikesTable, Hashtable<String, List<String>> swiperTable){
        if(leftorright.equals("left")){
            //dislikes
            dislikesTable.put(swipee, dislikesTable.getOrDefault(swipee, 0) + 1);

        }else if(leftorright.equals("right")){
            //likes
            likesTable.put(swipee, likesTable.getOrDefault(swipee, 0) + 1);

            if(!swiperTable.containsKey(swipee)) swiperTable.put(swipee, new ArrayList<String>());
            swiperTable.get(swipee).add(swiper);
        }
    }
}