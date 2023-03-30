import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import com.google.gson.Gson;
import org.apache.maven.lab1.Request;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.maven.lab1.SwipeDao;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;
import org.json.JSONObject;

@WebServlet(name = "MatchesServlet", value = "/matches/*")
public class MatchesServlet extends HttpServlet {
    private ConnectionFactory factory;
    private Connection connection;
    private BlockingQueue<Channel> channelPool;
    private static final String QUEUE_NAME = "my-queue";
    private static final int POOL_SIZE = 200;

    public void init() {
        factory = new ConnectionFactory();
        //factory.setHost("34.219.75.157");
        factory.setHost("localhost");

        try {
            connection = factory.newConnection();
            channelPool = new ArrayBlockingQueue<>(POOL_SIZE);
            for (int i = 0; i < POOL_SIZE; i++) {
                channelPool.offer(connection.createChannel());
            }
            System.out.println("Initialized RabbitMQ channel pool successfully.");
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Error initializing RabbitMQ channel pool", e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            // do any sophisticated processing with urlParts which contains all the url params
            // TODO: process url params in `urlParts`

            String id = urlParts[1];
            try {
                List<String> list = getSwipers(id);
                String str = "";
                for(String s : list){
                    str += s;
                    str += ",";
                }
                JSONObject json = new JSONObject();
                json.put("matches", str);
                res.setContentType("application/json");

                // Write the JSON to the response output stream
                PrintWriter out = res.getWriter();
                out.print(json.toString());


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getlikes(String id) throws SQLException {
        SwipeDao swipeDao = new SwipeDao();
        return swipeDao.getLikes(id);
    }

    public int getDislikes(String id) throws SQLException {
        SwipeDao swipeDao = new SwipeDao();
        return swipeDao.getDisLikes(id);
    }

    public List<String> getSwipers(String id) throws SQLException{
        SwipeDao swipeDao = new SwipeDao();
        return swipeDao.getAllSwiper(id);
    }

    private boolean isUrlValid(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
//        if(urlPath.length != 3){
//            return false;
//        }
//        if(!(urlPath[1].equals("likes") || urlPath[1].equals("dislikes") || urlPath[1].equals("swipers"))){
//            return false;
//        }
        return true;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        String s;

        String path = req.getPathInfo();
        String leftorright = "";
        if("/left/".equals(path)) leftorright = "left";
        else if("/right/".equals(path)) leftorright = "right";

        while ((s = req.getReader().readLine()) != null) {
            sb.append(s);
        }

        Request url = (Request) gson.fromJson(sb.toString(), Request.class);
        if (!isPostUrlValid(url)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
//            String message = "Hello World!";
//            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
//            System.out.println(" [x] Sent '" + message + "'");

            Channel channel = null;
            try {
                channel = channelPool.take();
                int index = sb.lastIndexOf("}");
                if (index != -1) {
                    if (index != -1) {
                        sb.insert(index, ",\"leftorright\":\"" + leftorright + "\"");
                    }
                }
                channel.basicPublish("", QUEUE_NAME, null, sb.toString().getBytes());
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException("Error publishing message to RabbitMQ", e);
            } finally {
                if (channel != null) {
                    channelPool.offer(channel);
                }
            }

            res.setStatus(HttpServletResponse.SC_OK);
            // do any sophisticated processing with urlParts which contains all the url params
            // TODO: process url params in `urlParts`
            res.getWriter().write("It works!");
        }
    }

    private boolean isPostUrlValid(Request url) {
        int swiper = Integer.parseInt(url.getSwiper());
        int swipee = Integer.parseInt(url.getSwipee());
        String comment = url.getComment();
        if(
                ((swiper >= 0) && (swiper <= 5000)) &&
                        ((swipee >= 0) && (swipee <= 1000000)) &&
                        comment.length() <= 256
        ){
            return true;
        }
        return false;
    }
}
