package cn.iocoder.yudao.module.ziwei.engine.cycle;

import cn.iocoder.yudao.module.ziwei.engine.model.*;
import cn.iocoder.yudao.module.ziwei.engine.star.BrightnessCalculator;
import cn.iocoder.yudao.module.ziwei.engine.star.SihuaCalculator;
import cn.iocoder.yudao.module.ziwei.enums.*;

import java.util.*;

/**
 * 流年计算引擎
 * <p>
 * 计算指定年份的流年命宫、四化、星曜旋转位置等完整流年信息。
 *
 * @author JTWORLD
 */
public class LiuninEngine {

    private final SihuaCalculator sihuaCalculator = new SihuaCalculator();
    private final BrightnessCalculator brightnessCalculator = new BrightnessCalculator();

    /**
     * 计算指定流年的完整信息
     *
     * @param chart 本命盘
     * @param year  流年年份
     * @return 完整流年结果
     */
    public LiuninResult calculateLiunin(ChartContext chart, int year) {
        // 1. 计算该年天干地支（按60甲子循环偏移）
        int offset = year - chart.getSolarYear();
        int yearGanIndex = ((chart.getYearTianGan().getIndex() + offset) % 10 + 10) % 10;
        int yearZhiIndex = ((chart.getYearDiZhi().getIndex() + offset) % 12 + 12) % 12;

        TianGanEnum liunianGan = TianGanEnum.ofIndex(yearGanIndex);
        DiZhiEnum liunianZhi = DiZhiEnum.ofIndex(yearZhiIndex);

        // 2. 流年命宫：该流年地支对应的宫位
        Palace liunianMingPalace = findPalaceByDiZhi(chart.getPalaces(), liunianZhi);

        // 3. 流年四化
        List<StarPosition> liunianSihua = sihuaCalculator.calculateLiunianSihua(liunianGan);

        // 4. 流年星曜旋转：各星曜从本命地支偏移至流年地支
        Map<String, DiZhiEnum> liunianStarPositions = new HashMap<>();
        Map<String, StarPosition> liunianStarDetails = new HashMap<>();

        // 生年地支与流年地支的偏移量
        int branchOffset = (liunianZhi.getIndex() - chart.getYearDiZhi().getIndex() + 12) % 12;

        // 遍历所有宫位的所有星曜，计算流年位置
        for (Palace palace : chart.getPalaces().values()) {
            for (StarPosition natalSp : palace.getStars()) {
                // 星曜的本命地支
                DiZhiEnum natalDiZhi = palace.getDiZhi();
                // 流年地支 = 本命地支顺时针旋转 offset 位
                DiZhiEnum liunianDiZhi = natalDiZhi.moveClockwise(branchOffset);

                liunianStarPositions.put(natalSp.getStarCode(), liunianDiZhi);

                // 计算流年位置的亮度
                StarBrightnessEnum liunianBrightness = brightnessCalculator.calculate(natalSp.getStarCode(), liunianDiZhi);

                StarPosition liunianSp = StarPosition.builder()
                        .starCode(natalSp.getStarCode())
                        .starName(natalSp.getStarName())
                        .starType(natalSp.getStarType())
                        .brightness(liunianBrightness)
                        .sihuaType(null) // 流年四化在下方叠加
                        .build();
                liunianStarDetails.put(natalSp.getStarCode(), liunianSp);
            }
        }

        // 5. 叠加流年四化到星曜
        for (StarPosition sihua : liunianSihua) {
            StarPosition existing = liunianStarDetails.get(sihua.getStarCode());
            if (existing != null) {
                existing.setSihuaType(sihua.getSihuaType());
            }
        }

        // 6. 构建结果
        LiuninResult result = LiuninResult.builder()
                .liunianYear(year)
                .liunianGan(liunianGan)
                .liunianZhi(liunianZhi)
                .liunianMingPalaceType(liunianMingPalace != null ? liunianMingPalace.getType() : null)
                .liunianMingPalaceName(liunianMingPalace != null ? liunianMingPalace.getType().getName() : null)
                .liunianSihua(liunianSihua)
                .liunianStarPositions(liunianStarPositions)
                .liunianStarDetails(liunianStarDetails)
                .build();

        return result;
    }

    /**
     * 计算流年范围内的所有流年信息
     *
     * @param chart     本命盘
     * @param startYear 起始年份
     * @param endYear   结束年份
     * @return 流年结果列表
     */
    public List<LiuninResult> calculateLiuninRange(ChartContext chart, int startYear, int endYear) {
        List<LiuninResult> results = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            results.add(calculateLiunin(chart, year));
        }
        return results;
    }

    /**
     * 根据地支查找对应的宫位
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
