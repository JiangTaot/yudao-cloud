package cn.iocoder.yudao.module.ziwei.framework.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 紫微斗数 AI 配置属性
 * <p>
 * 使用自定义前缀 {@code ziwei.ai}，与 Spring AI 标准属性分离，
 * 避免自动配置属性绑定不稳定的问题。
 *
 * @author JTWORLD
 */
@ConfigurationProperties(prefix = "ziwei.ai")
@Data
public class ZiweiAiProperties {

    private Deepseek deepseek = new Deepseek();
    private Embedding embedding = new Embedding();

    @Data
    public static class Deepseek {
        /** DeepSeek API Key */
        private String apiKey;
        /** API 地址，默认 DeepSeek */
        private String baseUrl = "https://api.deepseek.com";
        /** 模型名 */
        private String model = "deepseek-v4-pro";
        /** 温度 */
        private Double temperature = 0.7;
        /** 最大 Token */
        private Integer maxTokens = 4096;
    }

    @Data
    public static class Embedding {
        /** 阿里云 DashScope API Key */
        private String apiKey;
        /** Embedding 模型名 */
        private String model = "text-embedding-v4";
    }

}
