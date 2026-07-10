package cn.iocoder.yudao.module.ziwei.engine.palace;

import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;

/**
 * 五行局计算器
 * <p>
 * 根据命宫天干地支，用纳音五行取数法计算五行局。
 * <p>
 * 算法（参考 iztro / 紫微斗数全书）：
 * <pre>
 * 天干取数：甲乙=1, 丙丁=2, 戊己=3, 庚辛=4, 壬癸=5
 * 地支取数：子午丑未=1, 寅申卯酉=2, 辰戌巳亥=3
 * 干支数相加，超过5者减去5，以差论之：
 *   1=木三局, 2=金四局, 3=水二局, 4=火六局, 5=土五局
 * </pre>
 *
 * @author JTWORLD
 */
public class WuxingJuCalculator {

    /**
     * 计算五行局数值
     *
     * @param mingGongTianGan 命宫天干
     * @param mingGongDiZhi   命宫地支
     * @return 五行局数值（水=2, 木=3, 金=4, 土=5, 火=6）
     */
    public int calculateJuNumber(TianGanEnum mingGongTianGan, DiZhiEnum mingGongDiZhi) {
        // 天干取数：甲乙=1, 丙丁=2, 戊己=3, 庚辛=4, 壬癸=5
        int stemNumber = mingGongTianGan.getIndex() / 2 + 1;

        // 地支取数：子午丑未=1, 寅申卯酉=2, 辰戌巳亥=3
        // 将地支索引映射到 0-5 范围内再取数
        int branchMod = mingGongDiZhi.getIndex() % 6;
        int branchNumber = branchMod / 2 + 1;

        // 干支数相加，超过5者减去5
        int sum = stemNumber + branchNumber;
        while (sum > 5) {
            sum -= 5;
        }

        // 1=木三局(3), 2=金四局(4), 3=水二局(2), 4=火六局(6), 5=土五局(5)
        return switch (sum) {
            case 1 -> 3; // 木三局
            case 2 -> 4; // 金四局
            case 3 -> 2; // 水二局
            case 4 -> 6; // 火六局
            case 5 -> 5; // 土五局
            default -> 0;
        };
    }

    /**
     * 获取五行局名称
     */
    public String getJuName(TianGanEnum mingGongTianGan, DiZhiEnum mingGongDiZhi) {
        int num = calculateJuNumber(mingGongTianGan, mingGongDiZhi);
        return switch (num) {
            case 2 -> "水二局";
            case 3 -> "木三局";
            case 4 -> "金四局";
            case 5 -> "土五局";
            case 6 -> "火六局";
            default -> "未知";
        };
    }

}
