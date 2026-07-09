package cn.iocoder.yudao.module.ziwei.engine.palace;

import cn.iocoder.yudao.module.ziwei.engine.model.Palace;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.PalaceTypeEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;

import java.util.EnumMap;
import java.util.Map;

/**
 * 十二宫排列引擎
 * <p>
 * 从命宫开始，逆时针排列十二宫：
 * 命宫→兄弟→夫妻→子女→财帛→疾厄→迁移→交友→官禄→田宅→福德→父母
 * <p>
 * 各宫干支：使用五虎遁法根据生年天干 + 该宫地支确定
 *
 * @author JTWORLD
 */
public class PalaceSetupEngine {

    /**
     * 建立十二宫（不含星曜，仅宫位结构）
     *
     * @param mingGongDiZhi 命宫所在地支
     * @param shenGongDiZhi 身宫所在地支
     * @param yearTianGan   生年天干
     * @return 十二宫 Map，key=宫位类型
     */
    public EnumMap<PalaceTypeEnum, Palace> setupPalaces(
            DiZhiEnum mingGongDiZhi,
            DiZhiEnum shenGongDiZhi,
            TianGanEnum yearTianGan) {

        EnumMap<PalaceTypeEnum, Palace> palaces = new EnumMap<>(PalaceTypeEnum.class);

        // 十二宫类型按逆时针排列顺序
        PalaceTypeEnum[] types = PalaceTypeEnum.values();

        // 从命宫地支开始，逆时针依次分配
        DiZhiEnum currentDiZhi = mingGongDiZhi;
        for (int i = 0; i < 12; i++) {
            PalaceTypeEnum type = types[i];
            boolean isShenGong = currentDiZhi == shenGongDiZhi;

            // 宫干：用五虎遁法计算
            TianGanEnum palaceGan = calcPalaceGanByWuHuDun(yearTianGan, currentDiZhi);

            Palace palace = Palace.builder()
                    .type(type)
                    .diZhi(currentDiZhi)
                    .tianGan(palaceGan)
                    .isShenGong(isShenGong)
                    .build();

            palaces.put(type, palace);

            // 逆时针移动至下一个宫位
            currentDiZhi = currentDiZhi.moveCounterClockwise(1);
        }

        return palaces;
    }

    /**
     * 五虎遁法计算宫干
     * <p>
     * 根据生年天干和该宫的地支，计算该宫的天干
     * 原理同月干计算：寅宫为起始，五虎遁确定寅宫的宫干
     */
    public static TianGanEnum calcPalaceGanByWuHuDun(TianGanEnum yearGan, DiZhiEnum diZhi) {
        // 寅宫（index=2）对应的宫干
        TianGanEnum yinGan = switch (yearGan) {
            case JIA, JI -> TianGanEnum.BING;   // 甲己 → 丙寅
            case YI, GENG -> TianGanEnum.WU;     // 乙庚 → 戊寅
            case BING, XIN -> TianGanEnum.GENG;  // 丙辛 → 庚寅
            case DING, REN -> TianGanEnum.REN;   // 丁壬 → 壬寅
            case WU, GUI -> TianGanEnum.JIA;     // 戊癸 → 甲寅
        };

        // 从寅宫到目标地支的偏移量
        int offset = (diZhi.getIndex() - DiZhiEnum.YIN.getIndex() + 12) % 12;
        return yinGan.ofIndex((yinGan.getIndex() + offset) % 10);
    }

}
