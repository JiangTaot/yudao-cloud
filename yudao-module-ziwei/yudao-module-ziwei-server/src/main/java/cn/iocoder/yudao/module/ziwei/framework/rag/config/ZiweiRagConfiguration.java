package cn.iocoder.yudao.module.ziwei.framework.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * RAG 相关配置（MinIO S3Client + 其他）
 *
 * @author JTWORLD
 */
@Configuration
@Slf4j
public class ZiweiRagConfiguration {

    @Value("${ziwei.rag.minio.endpoint:http://127.0.0.1:9000}")
    private String endpoint;

    @Value("${ziwei.rag.minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${ziwei.rag.minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${ziwei.rag.minio.bucket:ziwei-books}")
    private String bucket;

    @Value("${ziwei.rag.minio.region:us-east-1}")
    private String region;

    public String getBucket() {
        return bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    /**
     * 创建 MinIO S3Client Bean
     */
    @Bean
    public S3Client ziweiS3Client() {
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)  // MinIO 需要 path-style 访问
                        .build())
                .build();
        log.info("[ziweiS3Client] MinIO S3Client 初始化完成, endpoint={}, bucket={}", endpoint, bucket);
        return client;
    }

}
