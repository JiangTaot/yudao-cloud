package cn.iocoder.yudao.module.ziwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 命盘摘要 DTO（供 Feign 远程调用返回）
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiChartSummaryDTO {

    @Schema(description = "命盘ID")
    private Long id;

    @Schema(description = "年柱", example = "庚午")
    private String yearPillar;

    @Schema(description = "月柱", example = "壬午")
    private String monthPillar;

    @Schema(description = "日柱", example = "辛亥")
    private String dayPillar;

    @Schema(description = "时柱", example = "乙未")
    private String hourPillar;

    @Schema(description = "命宫地支", example = "寅")
    private String mingGongDizhi;

    @Schema(description = "身宫地支", example = "子")
    private String shenGongDizhi;

    @Schema(description = "五行局", example = "木三局")
    private String wuxingJu;

    @Schema(description = "生年天干", example = "庚")
    private String yearTianGan;

    @Schema(description = "生年地支", example = "午")
    private String yearDiZhi;

}
