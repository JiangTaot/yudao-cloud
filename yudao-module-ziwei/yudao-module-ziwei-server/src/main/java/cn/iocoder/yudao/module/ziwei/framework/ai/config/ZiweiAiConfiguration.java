package cn.iocoder.yudao.module.ziwei.framework.ai.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 紫微斗数 AI 配置
 * <p>
 * 参考 yudao-module-ai 的 {@code AiAutoConfiguration}，
 * 手动创建 ChatModel Bean，避免依赖 Spring AI 自动配置的属性绑定不确定性问题。
 * <p>
 * Embedding 暂未接入（TODO：后续配置阿里云 DashScope / SiliconFlow 等）,
 * RAG 相关代码在 {@code ZiweiRagServiceImpl} 中已预留。
 *
 * @author JTWORLD
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ZiweiAiProperties.class)
@Slf4j
public class ZiweiAiConfiguration {

    @Bean
    public ChatModel chatModel(ZiweiAiProperties properties) {
        ZiweiAiProperties.Deepseek deepseekProps = properties.getDeepseek();
        log.info("[chatModel] 初始化 DeepSeek, baseUrl={}, model={}", deepseekProps.getBaseUrl(), deepseekProps.getModel());

        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(deepseekProps.getBaseUrl())
                .apiKey(deepseekProps.getApiKey())
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(deepseekProps.getModel())
                        .temperature(deepseekProps.getTemperature())
                        .maxTokens(deepseekProps.getMaxTokens())
                        .build())
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel(ZiweiAiProperties properties) {
        ZiweiAiProperties.Embedding embedProps = properties.getEmbedding();
        log.info("[embeddingModel] 初始化 DashScope, model={}", embedProps.getModel());

        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(embedProps.getApiKey())
                .build();

        DashScopeEmbeddingOptions options = DashScopeEmbeddingOptions.builder()
                .withModel(embedProps.getModel())
                .build();

        return new DashScopeEmbeddingModel(dashScopeApi, MetadataMode.EMBED, options);
    }

    @Bean
    public BatchingStrategy batchingStrategy() {
        return new TokenCountBatchingStrategy();
    }

}
