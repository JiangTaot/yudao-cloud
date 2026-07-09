package cn.iocoder.yudao.module.ziwei.engine.cycle;

import cn.iocoder.yudao.module.ziwei.engine.model.ChartContext;
import cn.iocoder.yudao.module.ziwei.engine.model.Palace;
import cn.iocoder.yudao.module.ziwei.engine.model.StarPosition;
import cn.iocoder.yudao.module.ziwei.engine.star.SihuaCalculator;
import cn.iocoder.yudao.module.ziwei.enums.*;

import java.util.*;

/**
 * 流年计算引擎
 * <p>
 * 计算指定年份的流年星曜和四化叠加
 *
 * @author JTWORLD
 */
public class LiuninEngine {

    private final SihuaCalculator sihuaCalculator = new SihuaCalculator();

    /**
     * 计算指定流年的信息
     * <p>
     * 流年命宫：以该年地支在十二宫中的位置为命宫
     * 流年四化：以该年天干计算
     *
     * @param chart    本命盘
     * @param year     流年年份
     * @return 流年四化列表
     */
    public List<StarPosition> calculateLiunin(ChartContext chart, int year) {
        // 1. 计算该年天干地支
        // 年的天干地支按60甲子循环
        int offset = year - chart.getSolarYear();
        int yearGanIndex = (chart.getYearTianGan().getIndex() + offset) % 10;
        if (yearGanIndex < 0) yearGanIndex += 10;
        int yearZhiIndex = (chart.getYearDiZhi().getIndex() + offset) % 12;
        if (yearZhiIndex < 0) yearZhiIndex += 12;

        TianGanEnum liunianGan = TianGanEnum.ofIndex(yearGanIndex);
        DiZhiEnum liunianZhi = DiZhiEnum.ofIndex(yearZhiIndex);

        // 2. 流年四化
        List<StarPosition> liunianSihua = sihuaCalculator.calculateLiunianSihua(liunianGan);

        // 3. 流年命宫：该流年地支对应的宫位
        Palace liunianMingPalace = findPalaceByDiZhi(chart.getPalaces(), liunianZhi);

        return liunianSihua;
    }

    /**
     * 根据地支查找宫位
     */
    private Palace findPalaceByDiZhi(EnumMap<PalaceTypeEnum, Palace> palaces, DiZhiEnum diZhi) {
        for (Palace palace : palaces.values()) {
            if (palace.getDiZhi() == diZhi) {
                return palace;
            }
        }
        return null;
    }

}
