package cn.iocoder.yudao.module.ziwei.framework.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 紫微斗数 AI 配置
 * <p>
 * 手动创建 ChatModel Bean 连接 DeepSeek，用于 AI 命盘解读。
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

}
