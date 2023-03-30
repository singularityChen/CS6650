import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.SwipeDetails;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SendRequest implements Runnable  {

    static int NUM_TRIES = 5;
    static int MAX_CHAR_LENGTH = 256;
    static int LOWER_CASE_ALPHABET_CHAR_LIMIT = 26;

    static String PATH = "http://localhost:8080/lab1_war_exploded/";
    //static String PATH = "http://35.87.72.9:8080/lab1_war/";
    //static String PATH = "http://35.93.19.153:8080/lab1_war/";
    //static String PATH = "http://LoadbalancerForServers-2090181816.us-west-2.elb.amazonaws.com/lab1_war/";
    //LoadbalancerForServers-2090181816.us-west-2.elb.amazonaws.com
    static AtomicInteger NUM_SUCCESS_REQ = new AtomicInteger(0);
    static AtomicInteger NUM_UNSUCCESS_REQ = new AtomicInteger(0);

    //static List<String> REQUEST_RECORDS = new ArrayList<String>();
    //static List<Long> RESPONSE_TIMES = new ArrayList<Long>();
    static BlockingQueue<String> REQUEST_RECORDS = new LinkedBlockingQueue<>();
    static BlockingQueue<Long> RESPONSE_TIMES = new LinkedBlockingQueue<>();
    static AtomicLong TOTAL_TIME = new AtomicLong(0);
    static int NUM_THREADS = 200;
    static int NUM_REQUESTS = 250;
    static Timestamp startTime;
    public SendRequest(int NUM_REQUESTS){
        this.NUM_REQUESTS = NUM_REQUESTS;
    }

    public static void main(String[] args) {
        //start timestamp
        startTime = Timestamp.from(Instant.now());

        //new
        ExecutorService postExecutor = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            postExecutor.submit(new SendRequest(NUM_REQUESTS));
        }
        postExecutor.shutdown();

        Thread getThread = new Thread(new GetThread());
        getThread.start();

        try {
            postExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            getThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //show posting performance
        showOutput();

    }

    public void run(){
        sendPostRequest();
    }

    private static class GetThread implements Runnable {
        private final List<Long> latencies = new ArrayList<>();

        int NUM_GET_REQUESTS = 5;
        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(PATH);
            StatsApi statsApi = new StatsApi(apiClient);
            MatchesApi matchesApi = new MatchesApi(apiClient);

            try {
                long startTime = System.nanoTime();

                for(int i = 0; i < NUM_GET_REQUESTS; i++){
                    int randomNumber = new Random().nextInt(5000) + 1;
                    String ID = Integer.toString(randomNumber);
                    MatchStats matchStats = statsApi.matchStatsWithHttpInfo(ID).getData();
//                    System.out.println("Dislikes: "+matchStats.getNumDislikes());
//                    System.out.println("Likes: "+matchStats.getNumLlikes());

                }

                long endTime = System.nanoTime();

                long latency = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
                latencies.add(latency);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000); // Wait for 1 second between requests
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            long minLatency = latencies.stream().mapToLong(Long::longValue).min().orElse(0);
            long meanLatency = (long) latencies.stream().mapToLong(Long::longValue).average().orElse(0);
            long maxLatency = latencies.stream().mapToLong(Long::longValue).max().orElse(0);
            System.out.printf("Min latency: %dms, Mean latency: %dms, Max latency: %dms%n", minLatency, meanLatency, maxLatency);
        }
    }

    public static void showOutput(){
        //calculate the output.
        Timestamp endTime = Timestamp.from(Instant.now());
        long wall_time = endTime.getTime() - startTime.getTime();

        System.out.println("Successful requests sent: "+NUM_SUCCESS_REQ);
        System.out.println("Unsuccessful requests sent: "+NUM_UNSUCCESS_REQ);
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

    public static void sendPostRequest(){
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
