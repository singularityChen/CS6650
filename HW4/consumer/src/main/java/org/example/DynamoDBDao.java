package org.example;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class DynamoDBDao {
    private static DynamoDbClient dynamoDbClient;

    public DynamoDBDao() {
        dynamoDbClient = DynamoDBClientManager.getClient();
    }

    public void createSwipeData(Swipe swipe) {
        Map<String, AttributeValue> item = new HashMap<>();

        UUID uuid = UUID.randomUUID();
        item.put("id", AttributeValue.builder().s(uuid.toString()).build());
        item.put("swiper", AttributeValue.builder().s(swipe.getSwiper()).build());
        item.put("swipee", AttributeValue.builder().s(swipe.getSwipee()).build());
        item.put("leftOrRight", AttributeValue.builder().s(swipe.getLeftOrRight()).build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("swipeData")
                .item(item)
                .build();

        try {
            dynamoDbClient.putItem(putItemRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        DynamoDBDao dynamoDBDao = new DynamoDBDao();
        dynamoDBDao.createSwipeData(new Swipe("A", "B", "left"));
    }

}
