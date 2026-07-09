package cn.iocoder.yudao.module.ziwei.engine.cycle;

import cn.iocoder.yudao.module.ziwei.engine.model.DaxianCycle;
import cn.iocoder.yudao.module.ziwei.engine.model.Palace;
import cn.iocoder.yudao.module.ziwei.enums.*;

import java.util.*;

/**
 * 大限（十年运程）计算引擎
 * <p>
 * 根据性别、生年阴阳、五行局数确定大限方向和起始岁数
 *
 * @author JTWORLD
 */
public class DaxianEngine {

    /**
     * 计算所有大限周期
     *
     * @param palaces        十二宫（已建立，含宫位信息）
     * @param mingGongDiZhi  命宫地支
     * @param gender         性别
     * @param yearTianGan    生年天干（用于判断阴阳）
     * @param wuxingJuNumber 五行局数（起始岁数）
     * @param birthYear      公历出生年
     * @return 大限列表（12条记录）
     */
    public List<DaxianCycle> calculate(EnumMap<PalaceTypeEnum, Palace> palaces,
                                        DiZhiEnum mingGongDiZhi,
                                        GenderEnum gender,
                                        TianGanEnum yearTianGan,
                                        int wuxingJuNumber,
                                        int birthYear) {

        // 1. 判断大限方向
        boolean isYangMale = gender == GenderEnum.MALE && "阳".equals(yearTianGan.getYinYang());
        boolean isYinFemale = gender == GenderEnum.FEMALE && "阴".equals(yearTianGan.getYinYang());
        boolean forward = isYangMale || isYinFemale; // 阳男阴女→顺行

        // 2. 起始岁数 = 五行局数
        int startAge = wuxingJuNumber;

        // 3. 按方向遍历十二宫，生成大限
        List<DaxianCycle> cycles = new ArrayList<>();
        DiZhiEnum currentDiZhi = mingGongDiZhi;

        for (int i = 0; i < 12; i++) {
            int ageStart = startAge + i * 10;
            int ageEnd = ageStart + 9;

            // 找到当前地支对应的宫位
            PalaceTypeEnum palaceType = findPalaceByDiZhi(palaces, currentDiZhi);

            DaxianCycle cycle = DaxianCycle.builder()
                    .sequenceOrder(i + 1)
                    .ageStart(ageStart)
                    .ageEnd(ageEnd)
                    .calYearStart(birthYear + ageStart - 1)
                    .calYearEnd(birthYear + ageEnd - 1)
                    .palaceType(palaceType)
                    .palaceDiZhi(currentDiZhi.getName())
                    .forward(forward)
                    .build();

            cycles.add(cycle);

            // 更新对应宫位的大限信息
            if (palaceType != null) {
                Palace palace = palaces.get(palaceType);
                if (palace != null) {
                    palace.setDaXianStartAge(ageStart);
                    palace.setDaXianEndAge(ageEnd);
                }
            }

            // 移动到下一个宫位
            currentDiZhi = forward
                    ? currentDiZhi.moveClockwise(1)
                    : currentDiZhi.moveCounterClockwise(1);
        }

        return cycles;
    }

    /**
     * 根据地支查找对应的宫位类型
     */
    private PalaceTypeEnum findPalaceByDiZhi(EnumMap<PalaceTypeEnum, Palace> palaces, DiZhiEnum diZhi) {
        for (Map.Entry<PalaceTypeEnum, Palace> entry : palaces.entrySet()) {
            if (entry.getValue().getDiZhi() == diZhi) {
                return entry.getKey();
            }
        }
        return null;
    }

}
