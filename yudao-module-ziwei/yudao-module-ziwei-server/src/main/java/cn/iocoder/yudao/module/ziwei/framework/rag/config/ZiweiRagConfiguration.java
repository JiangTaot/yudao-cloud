package cn.iocoder.yudao.module.ziwei.framework.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.util.concurrent.Executor;

/**
 * RAG 相关配置（MinIO S3Client + 异步处理 + 其他）
 *
 * @author JTWORLD
 */
@Configuration
@EnableAsync
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
     * RAG 文档异步处理的线程池
     * <p>
     * 古籍 PDF 解析 + 向量化耗时较长，使用独立线程池避免阻塞 HTTP 请求线程。
     */
    @Bean(name = "ragProcessExecutor")
    public Executor ragProcessExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("rag-process-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("[ragProcessExecutor] RAG 异步处理线程池已初始化: corePool=2, maxPool=4, queueCapacity=100");
        return executor;
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
