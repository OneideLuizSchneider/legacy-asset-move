package com.legacy.asset.move.config;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionS3 {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private AmazonS3 s3;

    public ConnectionS3() {
        this.createS3Connection();
    }

    /**
     * <p>
     * This method will copy from the origin object to the destination
     * </p>
     */
    public boolean copyObject(String origin, String destination) {
        try {
            this.s3.copyObject(
                    dotenv.get("AWS_S3_BUCKET_ORIGIN"), origin,
                    dotenv.get("AWS_S3_BUCKET_DESTINATION"), destination
            );
            return true;
        } catch (AmazonServiceException e) {
            log.error("Error on method ConnectionS3.copyObject--------");
            log.error(e.getMessage());
            log.error("-----------------------------------------------");
        }
        return false;
    }

    /**
     * <p>
     * This method will create a connection with AWS S3
     * </p>
     */
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
