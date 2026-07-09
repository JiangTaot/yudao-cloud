package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.engine.model.StarPosition;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 辅星（8颗）安放
 * <p>
 * 左辅、右弼、文昌、文曲、天魁、天钺、禄存、天马
 *
 * @author JTWORLD
 */
public class AuxiliaryStarPlacer {

    /**
     * 安放所有8颗辅星
     *
     * @param lunarMonth 农历月 (1-12)
     * @param hourZhi    出生时辰地支
     * @param yearTianGan 年干
     * @param yearDiZhi   年支
     * @return Map：(星曜编码 → 所在地支)
     */
    public Map<String, DiZhiEnum> placeAll(int lunarMonth, DiZhiEnum hourZhi,
                                            TianGanEnum yearTianGan, DiZhiEnum yearDiZhi) {
        Map<String, DiZhiEnum> result = new LinkedHashMap<>();

        result.put("zuofu", placeZuoFu(lunarMonth));
        result.put("youbi", placeYouBi(lunarMonth));
        result.put("wenchang", placeWenChang(hourZhi));
        result.put("wenqu", placeWenQu(hourZhi));
        result.put("tiankui", placeTianKui(yearTianGan));
        result.put("tianyue", placeTianYue(yearTianGan));
        result.put("lucun", placeLuCun(yearTianGan));
        result.put("tianma", placeTianMa(yearDiZhi));

        return result;
    }

    /**
     * 左辅：从辰宫起正月，顺数至生月
     */
    public DiZhiEnum placeZuoFu(int lunarMonth) {
        return DiZhiEnum.CHEN.moveClockwise(lunarMonth - 1);
    }

    /**
     * 右弼：从戌宫起正月，逆数至生月
     */
    public DiZhiEnum placeYouBi(int lunarMonth) {
        return DiZhiEnum.XU.moveCounterClockwise(lunarMonth - 1);
    }

    /**
     * 文昌：从戌宫起子时，逆数至生时
     */
    public DiZhiEnum placeWenChang(DiZhiEnum hourZhi) {
        int offset = hourZhi.getIndex(); // 子=0, 丑=1, ...
        return DiZhiEnum.XU.moveCounterClockwise(offset);
    }

    /**
     * 文曲：从辰宫起子时，顺数至生时
     */
    public DiZhiEnum placeWenQu(DiZhiEnum hourZhi) {
        int offset = hourZhi.getIndex();
        return DiZhiEnum.CHEN.moveClockwise(offset);
    }

    /**
     * 天魁：按年干定位
     * <p>
     * 甲戊庚 → 丑未（天魁在丑）; 乙己 → 子申（天魁在子）;
     * 丙丁 → 亥酉（天魁在亥）; 壬癸 → 卯巳（天魁在卯）; 辛 → 午寅（天魁在午）
     */
    public DiZhiEnum placeTianKui(TianGanEnum yearGan) {
        return switch (yearGan) {
            case JIA, WU, GENG -> DiZhiEnum.CHOU; // 甲戊庚 → 丑
            case YI, JI -> DiZhiEnum.ZI;           // 乙己 → 子
            case BING, DING -> DiZhiEnum.HAI;       // 丙丁 → 亥
            case XIN -> DiZhiEnum.WU;               // 辛 → 午
            case REN, GUI -> DiZhiEnum.MAO;          // 壬癸 → 卯
        };
    }

    /**
     * 天钺：按年干定位
     * <p>
     * 甲戊庚 → 丑未（天钺在未）; 乙己 → 子申（天钺在申）;
     * 丙丁 → 亥酉（天钺在酉）; 壬癸 → 卯巳（天钺在巳）; 辛 → 午寅（天钺在寅）
     */
    public DiZhiEnum placeTianYue(TianGanEnum yearGan) {
        return switch (yearGan) {
            case JIA, WU, GENG -> DiZhiEnum.WEI; // 甲戊庚 → 未
            case YI, JI -> DiZhiEnum.SHEN;        // 乙己 → 申
            case BING, DING -> DiZhiEnum.YOU;      // 丙丁 → 酉
            case XIN -> DiZhiEnum.YIN;              // 辛 → 寅
            case REN, GUI -> DiZhiEnum.SI;           // 壬癸 → 巳
        };
    }

    /**
     * 禄存：按年干定位（财星）
     * <p>
     * 甲寅 乙卯 丙巳 丁午 戊已 己午 庚申 辛酉 壬亥 癸子
     */
    public DiZhiEnum placeLuCun(TianGanEnum yearGan) {
        return switch (yearGan) {
            case JIA -> DiZhiEnum.YIN;   // 甲 → 寅
            case YI -> DiZhiEnum.MAO;    // 乙 → 卯
            case BING -> DiZhiEnum.SI;   // 丙 → 巳
            case DING, JI -> DiZhiEnum.WU; // 丁己 → 午
            case WU -> DiZhiEnum.SI;      // 戊 → 巳
            case GENG -> DiZhiEnum.SHEN;  // 庚 → 申
            case XIN -> DiZhiEnum.YOU;    // 辛 → 酉
            case REN -> DiZhiEnum.HAI;    // 壬 → 亥
            case GUI -> DiZhiEnum.ZI;     // 癸 → 子
        };
    }

    /**
     * 天马：按年支定位（驿马星）
     * <p>
     * 寅午戌年 → 天马在申
     * 申子辰年 → 天马在寅
     * 巳酉丑年 → 天马在亥
     * 亥卯未年 → 天马在巳
     */
    public DiZhiEnum placeTianMa(DiZhiEnum yearZhi) {
        return switch (yearZhi) {
            case YIN, WU, XU -> DiZhiEnum.SHEN;     // 寅午戌 → 申
            case SHEN, ZI, CHEN -> DiZhiEnum.YIN;   // 申子辰 → 寅
            case SI, YOU, CHOU -> DiZhiEnum.HAI;    // 巳酉丑 → 亥
            case HAI, MAO, WEI -> DiZhiEnum.SI;     // 亥卯未 → 巳
        };
    }

    /**
     * 创建星曜落位对象
     */
    public StarPosition createAuxiliaryStar(String starCode) {
        String starName = switch (starCode) {
            case "zuofu" -> "左辅"; case "youbi" -> "右弼";
            case "wenchang" -> "文昌"; case "wenqu" -> "文曲";
            case "tiankui" -> "天魁"; case "tianyue" -> "天钺";
            case "lucun" -> "禄存"; case "tianma" -> "天马";
            default -> starCode;
        };
        return StarPosition.builder()
                .starCode(starCode)
                .starName(starName)
                .starType(StarTypeEnum.AUXILIARY)
                .build();
    }

}
