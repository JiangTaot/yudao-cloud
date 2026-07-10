package cn.iocoder.yudao.module.ziwei.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ziwei.service.ai.ZiweiAiInterpretService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 用户端 - 紫微斗数 AI 解读 Controller
 *
 * @author JTWORLD
 */
@Tag(name = "用户端 - 紫微斗数 AI 解读")
@RestController
@RequestMapping("/ziwei/ai")
@Validated
public class ZiweiAiController {

    @Resource
    private ZiweiAiInterpretService aiService;

    @PostMapping("/interpret/{chartId}")
    @Operation(summary = "AI 解读整个命盘")
    public CommonResult<String> interpretChart(
            @PathVariable("chartId") Long chartId,
            @Parameter(description = "用户问题（可选）") @RequestParam(value = "question", required = false) String question) {
        return success(aiService.interpretChart(chartId, question));
    }

    @PostMapping(value = "/interpret/{chartId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI 流式解读整个命盘（SSE）")
    public Flux<String> interpretChartStream(
            @PathVariable("chartId") Long chartId,
            @Parameter(description = "用户问题（可选）") @RequestParam(value = "question", required = false) String question) {
        return aiService.interpretChartStream(chartId, question);
    }

    @PostMapping("/interpret/{chartId}/palace/{palaceType}")
    @Operation(summary = "AI 解读特定宫位")
    public CommonResult<String> interpretPalace(
            @PathVariable("chartId") Long chartId,
            @PathVariable("palaceType") int palaceType,
            @Parameter(description = "用户问题（可选）") @RequestParam(value = "question", required = false) String question) {
        return success(aiService.interpretPalace(chartId, palaceType, question));
    }

}
