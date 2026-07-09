package cn.iocoder.yudao.module.ziwei.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

/**
 * 紫微斗数命盘 DO
 *
 * @author JTWORLD
 */
@TableName(value = "ziwei_chart", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiChartDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    // 出生信息
    private Integer solarYear;
    private Integer solarMonth;
    private Integer solarDay;
    private Integer solarHour;
    private Integer solarMinute;
    private Integer gender;
    private String birthPlace;
    private Boolean isDst;

    // 农历
    private Integer lunarYear;
    private Integer lunarMonth;
    private Integer lunarDay;
    private Boolean isLeapMonth;

    // 八字
    private String yearPillar;
    private String monthPillar;
    private String dayPillar;
    private String hourPillar;

    // 命盘核心
    private String mingGongDizhi;
    private String shenGongDizhi;
    private String wuxingJu;

    // JSON 备查
    private String chartJson;

}
