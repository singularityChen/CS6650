import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import com.opencsv.CSVWriter;

public class SendRequest implements Runnable  {

    static int NUM_TRIES = 5;
    static int MAX_CHAR_LENGTH = 256;
    static int LOWER_CASE_ALPHABET_CHAR_LIMIT = 26;
    static String PATH = "http://54.189.175.55:8080/lab1_war";
    //localhost
    static AtomicInteger NUM_SUCCESS_REQ = new AtomicInteger(0);
    static AtomicInteger NUM_UNSUCCESS_REQ = new AtomicInteger(0);

    //static List<String> REQUEST_RECORDS = new ArrayList<String>();
    //static List<Long> RESPONSE_TIMES = new ArrayList<Long>();
    static BlockingQueue<String> REQUEST_RECORDS = new LinkedBlockingQueue<>();
    static BlockingQueue<Long> RESPONSE_TIMES = new LinkedBlockingQueue<>();
    static AtomicLong TOTAL_TIME = new AtomicLong(0);
    static int NUM_THREADS = 200;
    static int NUM_REQUESTS = 2500;
    public SendRequest(int NUM_REQUESTS){
        this.NUM_REQUESTS = NUM_REQUESTS;
    }

    public static void main(String[] args) {

        List<Thread> threads = new ArrayList<>();
        Timestamp startTime = Timestamp.from(Instant.now());
        for (int i = 0; i < NUM_THREADS; i++) {
            Thread thread = new Thread(new SendRequest(NUM_REQUESTS));
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Timestamp endTime = Timestamp.from(Instant.now());
        long wall_time = endTime.getTime() - startTime.getTime();

        System.out.println("Number of successful requests sent: "+NUM_SUCCESS_REQ);
        System.out.println("Number of unsuccessful requests sent: "+NUM_UNSUCCESS_REQ);
        System.out.println("Wall time: "+wall_time+" ms");
        System.out.println("Requests per second: "+ Long.valueOf(NUM_SUCCESS_REQ.addAndGet(0) + NUM_UNSUCCESS_REQ.addAndGet(0)) * 1.0 / wall_time * 1000);

        List<Long> REQUEST_RECORDS_LIST = new ArrayList<>();
        for(Long time : RESPONSE_TIMES){
            REQUEST_RECORDS_LIST.add(time);
        }

        // calculate mean
        double sum = 0;
        for (long l : REQUEST_RECORDS_LIST) {
            sum += l;
        }
        double mean = sum / REQUEST_RECORDS_LIST.size();

        // calculate median
        Collections.sort(REQUEST_RECORDS_LIST);
        long median;
        if (REQUEST_RECORDS_LIST.size() % 2 == 0) {
            median = (REQUEST_RECORDS_LIST.get(REQUEST_RECORDS_LIST.size() / 2) + REQUEST_RECORDS_LIST.get(REQUEST_RECORDS_LIST.size() / 2 - 1)) / 2;
        } else {
            median = REQUEST_RECORDS_LIST.get(REQUEST_RECORDS_LIST.size() / 2);
        }

        // calculate 99th percentile
        int percentileIndex = (int) Math.ceil(REQUEST_RECORDS_LIST.size() * 0.99);
        long percentile = REQUEST_RECORDS_LIST.get(percentileIndex - 1);

        // calculate min and max
        long min = Collections.min(REQUEST_RECORDS_LIST);
        long max = Collections.max(REQUEST_RECORDS_LIST);

        // print results
        System.out.println("Mean: " + mean + " ms");
        System.out.println("Median: " + median + " ms");
        System.out.println("Throughput: "+ Long.valueOf(NUM_SUCCESS_REQ.addAndGet(0) + NUM_UNSUCCESS_REQ.addAndGet(0)) * 1.0 / wall_time * 1000);
        System.out.println("99th percentile: " + percentile + " ms");
        System.out.println("Min: " + min + " ms");
        System.out.println("Max: " + max + " ms");

        //csv
        List<String> data = new ArrayList<>();
        for(String s : REQUEST_RECORDS){
            data.add(s);
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter("request_data.csv"))) {
            // Write the header row
            writer.writeNext(new String[] {"Start Time", "Request Type", "Latency", "Response Code"});

            // Write each data row to the CSV file
            for (String row : data) {
                String[] fields = row.split(",");
                writer.writeNext(fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){
        sendRequest();
    }

    public static void sendRequest(){
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(PATH);
        SwipeApi apiInstance = new SwipeApi(apiClient);
        SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details

        try {
            //apiInstance.swipe(body, leftorright);
            //System.out.println(apiInstance.swipeWithHttpInfo(body, leftorright).getStatusCode());
            int numOfSuccess = 0;
            int numOfUnsuccess = 0;
            long time = 0L;
            for(int i = 0; i < NUM_REQUESTS; i++){
                int retries = 0;
                while(true){
                    body.setSwiper(String.valueOf(randomNumber(5000)));
                    body.setSwipee(String.valueOf(randomNumber(1000000)));
                    body.setComment(randomCommentGenerator());
                    Timestamp startTime = Timestamp.from(Instant.now());
                    int status = apiInstance.swipeWithHttpInfo(body, randomLeftRight()).getStatusCode();
                    Timestamp endTime = Timestamp.from(Instant.now());
                    long timeDiff = endTime.getTime() - startTime.getTime();
                    REQUEST_RECORDS.add(startTime+","+"POST"+","+timeDiff+","+status);
                    RESPONSE_TIMES.add(timeDiff);
                    time+=timeDiff;
                    if (status == 200) {
                        numOfSuccess++;
                        //NUM_SUCCESS_REQ.addAndGet(1);
                        break;
                    } else {
                        retries++;
                        if (retries >= NUM_TRIES) {
                            numOfUnsuccess++;
                            //NUM_UNSUCCESS_REQ.addAndGet(1);
                            break;
                        }
                    }
                }
            }
            NUM_SUCCESS_REQ.addAndGet(numOfSuccess);
            NUM_UNSUCCESS_REQ.addAndGet(numOfUnsuccess);
            TOTAL_TIME.addAndGet(time);

        } catch (ApiException e) {
            System.err.println("Exception when calling SwipeApi#swipe");
            e.printStackTrace();
        }
    }

    public static int randomNumber(int maxNumber) {
        Random random = new Random();
        return random.nextInt(maxNumber+1);
    }

    public static String randomCommentGenerator() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        int totalLength = random.nextInt(MAX_CHAR_LENGTH + 1);
        for(int i = 0 ; i < totalLength; i++) {
            builder.append((char)(random.nextInt(LOWER_CASE_ALPHABET_CHAR_LIMIT) + 'a'));
        }
        return builder.toString();
    }

    public static String randomLeftRight() {
        Random r = new Random();
        double d1 = r.nextDouble();
        if(d1 < 0.5) return "left";
        else return "right";
    }


}
