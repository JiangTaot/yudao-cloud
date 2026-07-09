package cn.iocoder.yudao.module.ziwei.engine.model;

import cn.iocoder.yudao.module.ziwei.enums.PalaceTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大限（十年运程周期）
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaxianCycle {

    /** 序号 1-12 */
    private int sequenceOrder;

    /** 起始虚岁 */
    private int ageStart;

    /** 结束虚岁 */
    private int ageEnd;

    /** 公历起始年份 */
    private int calYearStart;

    /** 公历结束年份 */
    private int calYearEnd;

    /** 所在宫位类型 */
    private PalaceTypeEnum palaceType;

    /** 宫位地支 */
    private String palaceDiZhi;

    /** 方向：true=顺行(顺时针)，false=逆行(逆时针) */
    private boolean forward;

    @Override
    public String toString() {
        return String.format("%d-%d岁 %s宫（%s）", ageStart, ageEnd, palaceType.getName(),
                forward ? "顺行" : "逆行");
    }

}
