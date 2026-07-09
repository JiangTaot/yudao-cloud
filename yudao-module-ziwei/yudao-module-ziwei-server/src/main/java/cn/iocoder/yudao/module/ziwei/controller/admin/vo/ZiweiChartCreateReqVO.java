package cn.iocoder.yudao.module.ziwei.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 命盘创建请求 VO
 *
 * @author JTWORLD
 */
@Data
public class ZiweiChartCreateReqVO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "公历出生年份", requiredMode = Schema.RequiredMode.REQUIRED, example = "1990")
    @NotNull(message = "出生年份不能为空")
    private Integer solarYear;

    @Schema(description = "公历出生月份", requiredMode = Schema.RequiredMode.REQUIRED, example = "6")
    @NotNull @Min(1) @Max(12)
    private Integer solarMonth;

    @Schema(description = "公历出生日", requiredMode = Schema.RequiredMode.REQUIRED, example = "15")
    @NotNull @Min(1) @Max(31)
    private Integer solarDay;

    @Schema(description = "公历出生小时", requiredMode = Schema.RequiredMode.REQUIRED, example = "14")
    @NotNull @Min(0) @Max(23)
    private Integer solarHour;

    @Schema(description = "公历出生分钟", example = "30")
    @Min(0) @Max(59)
    private Integer solarMinute;

    @Schema(description = "性别：0-女 1-男", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull @Min(0) @Max(1)
    private Integer gender;

    @Schema(description = "出生地点", example = "北京")
    private String birthPlace;

    @Schema(description = "是否夏令时", example = "false")
    private Boolean isDst;

}
