package cn.iocoder.yudao.module.ziwei.engine.palace;

import cn.iocoder.yudao.module.ziwei.engine.model.NaYinData;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;

/**
 * 五行局计算器
 * <p>
 * 根据命宫的天干和地支，查六十纳音表确定五行局
 *
 * @author JTWORLD
 */
public class WuxingJuCalculator {

    /**
     * 计算五行局
     *
     * @param mingGongTianGan 命宫天干
     * @param mingGongDiZhi   命宫地支
     * @return 五行局数值（水=2, 木=3, 金=4, 土=5, 火=6）
     */
    public int calculateJuNumber(TianGanEnum mingGongTianGan, DiZhiEnum mingGongDiZhi) {
        return NaYinData.getWuxingJuNumber(mingGongTianGan, mingGongDiZhi);
    }

    /**
     * 获取五行局名称
     */
    public String getJuName(TianGanEnum mingGongTianGan, DiZhiEnum mingGongDiZhi) {
        return NaYinData.getWuxingJuName(mingGongTianGan, mingGongDiZhi);
    }

}
