package cn.iocoder.yudao.module.ziwei.engine.palace;

import cn.iocoder.yudao.module.ziwei.engine.model.Pillar;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;

/**
 * 命宫 + 身宫 定位计算器
 * <p>
 * 算法（参考 iztro / 紫微斗数全书）：
 * <pre>
 * 命宫：寅宫起正月，顺数至生月（顺时针），逆数生时为命宫（逆时针）
 *       公式: (lunarMonth + 1 - hourIndex + 12) % 12
 * 身宫：寅宫起正月，顺数至生月（顺时针），顺数生时为身宫（顺时针）
 *       公式: (lunarMonth + 1 + hourIndex) % 12
 * </pre>
 *
 * @author JTWORLD
 */
public class MingGongCalculator {

    /**
     * 计算命宫所在的地支
     * <p>
     * 口诀：寅宫起正月，顺数至生月，逆数生时为命宫
     *
     * @param lunarMonth 农历月 (1-12)
     * @param hourZhi    出生时辰地支
     * @return 命宫所在地支
     */
    public DiZhiEnum calculateMingGong(int lunarMonth, DiZhiEnum hourZhi) {
        // 1. 寅宫起正月，顺数至生月（顺时针）
        //    正月在寅，二月在卯，三月在辰，...
        //    → 顺时针移动 (lunarMonth - 1) 位
        DiZhiEnum monthDiZhi = DiZhiEnum.YIN.moveClockwise(lunarMonth - 1);

        // 2. 从该宫逆数生时（逆时针）
        //    子时在该宫，丑时在上一宫（逆时针），...
        //    → 逆时针移动 hourZhi.getIndex() 位
        return monthDiZhi.moveCounterClockwise(hourZhi.getIndex());
    }

    /**
     * 计算身宫所在的地支
     * <p>
     * 口诀：寅宫起正月，顺数至生月，顺数生时为身宫
     *
     * @param lunarMonth 农历月 (1-12)
     * @param hourZhi    出生时辰地支
     * @return 身宫所在地支
     */
    public DiZhiEnum calculateShenGong(int lunarMonth, DiZhiEnum hourZhi) {
        // 1. 寅宫起正月，顺数至生月（顺时针）
        //    正月在寅，二月在卯，三月在辰，...
        //    → 顺时针移动 (lunarMonth - 1) 位
        DiZhiEnum monthDiZhi = DiZhiEnum.YIN.moveClockwise(lunarMonth - 1);

        // 2. 从该宫顺数生时（顺时针）
        //    子时在该宫，丑时在下一宫（顺时针），...
        //    → 顺时针移动 hourZhi.getIndex() 位
        return monthDiZhi.moveClockwise(hourZhi.getIndex());
    }

    /**
     * 通过年月日时信息直接计算
     *
     * @param yearGan  年干
     * @param hourZhi  时辰地支
     * @return 命宫地支
     */
    public DiZhiEnum calculateMingGongByGanZhi(TianGanEnum yearGan, DiZhiEnum hourZhi) {
        // 简易查表法：命宫 = 寅 + (月支 - 时支)
        // 这里使用标准紫微斗数排盘口诀
        // 实际计算需要农历月数，这里只是参数占位
        return DiZhiEnum.YIN; // placeholder
    }

}
