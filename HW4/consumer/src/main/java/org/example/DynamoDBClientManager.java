package org.example;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;

public class DynamoDBClientManager {
    private static final String ACCESS_KEY = "ASIAWIOUGVFRE3A4WFDM";
    private static final String SECRET_KEY = "THGyfF2p/6H0udK2le+MUeiYTgGGanGiMuJJBPhG";

    private static final String sessionToken = "FwoGZXIvYXdzEEIaDMOgO5FngIKaGbSXOCLLAZpcn3hYQ9wSazMbuGvlVMTYYxBrVPlFSLjuMxEEH+ALuRSohQ0VpgCd0tfNR8qjULo0L0oybUoRDhhucVZ/IrjXodny1ZSpwKF4Ji1/ZFOQpedzGdo5TuB0xuD0ruO8omC4lcC1qeCT1A2iw6a6ZRxzkdiWwmkXYr7ZoJoUa/bs9KOjdI/i8BAUDcimETeCUrnW0FIxm6a1zaRVPPPo0GjrICNj07oN3+uuf4HTe7U1i1N6tR9unIZGys+Y8XkzEQ0GVyxHfTc21RS2KKX/pqIGMi17PylMPQecw/hhuUmHBrfg6ekJJZjdOz20lQqzCr/p6NEtm3prZcpcbBKY1BI=";
    private static final Region REGION = Region.US_WEST_2;

    private static DynamoDbClient dynamoDbClient;

    static {
        AwsSessionCredentials awsCredentials = AwsSessionCredentials.create(ACCESS_KEY, SECRET_KEY, sessionToken);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                awsCredentials);
        dynamoDbClient = DynamoDbClient.builder()
                .region(REGION)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    public static DynamoDbClient getClient() {
        return dynamoDbClient;
    }
}
