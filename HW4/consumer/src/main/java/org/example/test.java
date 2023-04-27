package org.example;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
public class test {
    // set AWS credentials
    public static void main(String[] args){
        // set AWS credentials
        // Set up the AWS credentials
        BasicAWSCredentials credentials = new BasicAWSCredentials("<your_access_key_id>", "<your_secret_access_key>");

// Set up the AWS client
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("<your_dynamodb_endpoint>", "<your_region>"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

// Set up the DynamoDB object
        DynamoDB dynamoDB = new DynamoDB(client);


    }
}
