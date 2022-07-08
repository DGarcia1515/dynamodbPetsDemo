package com.example.dynamodbpetsdemo.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.dynamodb.endpoint}")
    private String dynamodbEndpoint;
    @Value("${aws.dynamodb.accessKey}")
    private String awsAccessKey;
    @Value("${aws.dynamodb.secretKey}")
    private String awsSecretKey;

    @Value("${aws.dynamodb.signingRegion}")
    private String signingRegion;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(amazonDynamoDb());
    }

    private AmazonDynamoDB amazonDynamoDb() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamodbEndpoint,signingRegion))
                .withCredentials(amazonDynamoDBCredentials()).build();
    }

    private AWSCredentialsProvider amazonDynamoDBCredentials() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey,awsSecretKey));
    }
}
