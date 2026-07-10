package cn.iocoder.yudao.module.ziwei.service.ai;

import reactor.core.publisher.Flux;

/**
 * 紫微斗数 AI 命盘解读 Service 接口
 *
 * @author JTWORLD
 */
public interface ZiweiAiInterpretService {

    /**
     * 解读整个命盘
     *
     * @param chartId      命盘ID
     * @param userQuestion 用户问题（可选，传空则默认整体解读）
     * @return AI 解读结果
     */
    String interpretChart(Long chartId, String userQuestion);

    /**
     * 流式解读整个命盘
     *
     * @param chartId      命盘ID
     * @param userQuestion 用户问题
     * @return SSE 流式响应
     */
    Flux<String> interpretChartStream(Long chartId, String userQuestion);

    /**
     * 解读特定宫位
     *
     * @param chartId      命盘ID
     * @param palaceType   宫位类型 1-12
     * @param userQuestion 用户问题
     * @return AI 解读结果
     */
    String interpretPalace(Long chartId, int palaceType, String userQuestion);

}
