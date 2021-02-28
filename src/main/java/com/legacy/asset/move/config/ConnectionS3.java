package com.legacy.asset.move.config;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionS3 {

    private static final Dotenv dotenv = Dotenv.load();

    private AmazonS3 s3;

    public ConnectionS3() {
        this.createS3Connection();
    }

    public void copyObject(String origin, String destination) {
        try {
            this.s3.copyObject(
                    dotenv.get("AWS_S3_BUCKET_ORIGIN"), origin,
                    dotenv.get("AWS_S3_BUCKET_DESTINATION"), destination
            );
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
    }

    private void createS3Connection() {
        this.s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(
                                dotenv.get("AWS_S3_ACCESS_KEY"),
                                dotenv.get("AWS_S3_SECRET_KEY")
                        )
                ))
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                dotenv.get("AWS_S3_URL"),
                                dotenv.get("AWS_S3_REGION")
                        )
                ).build();
    }

}
