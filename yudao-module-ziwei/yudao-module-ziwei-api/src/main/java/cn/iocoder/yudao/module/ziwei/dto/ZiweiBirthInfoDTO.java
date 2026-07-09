package cn.iocoder.yudao.module.ziwei.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 出生信息 DTO（排盘输入参数）
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiBirthInfoDTO {

    @Schema(description = "公历出生年份", example = "1990")
    @NotNull(message = "出生年份不能为空")
    private Integer solarYear;

    @Schema(description = "公历出生月份", example = "6")
    @NotNull(message = "出生月份不能为空")
    @Min(value = 1, message = "出生月份应在 1-12 之间")
    @Max(value = 12, message = "出生月份应在 1-12 之间")
    private Integer solarMonth;

    @Schema(description = "公历出生日", example = "15")
    @NotNull(message = "出生日不能为空")
    @Min(value = 1, message = "出生日应在 1-31 之间")
    @Max(value = 31, message = "出生日应在 1-31 之间")
    private Integer solarDay;

    @Schema(description = "公历出生小时（0-23）", example = "14")
    @NotNull(message = "出生小时不能为空")
    @Min(value = 0, message = "出生小时应在 0-23 之间")
    @Max(value = 23, message = "出生小时应在 0-23 之间")
    private Integer solarHour;

    @Schema(description = "公历出生分钟（0-59）", example = "30")
    @Min(value = 0)
    @Max(value = 59)
    private Integer solarMinute;

    @Schema(description = "性别：0-女 1-男", example = "1")
    @NotNull(message = "性别不能为空")
    @Min(value = 0)
    @Max(value = 1)
    private Integer gender;

    @Schema(description = "出生地点", example = "北京")
    private String birthPlace;

    @Schema(description = "是否夏令时", example = "false")
    private Boolean isDst;

}
