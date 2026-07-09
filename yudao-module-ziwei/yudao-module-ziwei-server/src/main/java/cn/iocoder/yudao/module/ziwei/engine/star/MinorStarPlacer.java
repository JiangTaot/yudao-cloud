package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.engine.model.StarPosition;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 杂曜安放
 * <p>
 * 火星、铃星、擎羊、陀罗、地空、地劫、天刑、天姚、红鸾、天喜
 *
 * @author JTWORLD
 */
public class MinorStarPlacer {

    /**
     * 安放所有杂曜
     *
     * @param lunarMonth  农历月 (1-12)
     * @param hourZhi     时辰地支
     * @param yearDiZhi   年支
     * @param yearTianGan 年干
     * @return Map：(星曜编码 → 所在地支)
     */
    public Map<String, DiZhiEnum> placeAll(int lunarMonth, DiZhiEnum hourZhi,
                                            DiZhiEnum yearDiZhi, TianGanEnum yearTianGan) {
        Map<String, DiZhiEnum> result = new LinkedHashMap<>();

        result.put("huoxing", placeHuoXing(yearDiZhi, hourZhi));
        result.put("lingxing", placeLingXing(yearDiZhi, hourZhi));
        result.put("qingyang", placeQingYang(yearTianGan));
        result.put("tuoluo", placeTuoLuo(yearTianGan));
        result.put("dikong", placeDiKong(hourZhi));
        result.put("dijie", placeDiJie(hourZhi));
        result.put("tianxing", placeTianXing(lunarMonth));
        result.put("tianyao", placeTianYao(lunarMonth));
        result.put("hongluan", placeHongLuan(yearDiZhi));
        result.put("tianxi", placeTianXi(yearDiZhi));

        return result;
    }

    /**
     * 火星：按年支+时支定位
     */
    public DiZhiEnum placeHuoXing(DiZhiEnum yearZhi, DiZhiEnum hourZhi) {
        // 起始地支
        DiZhiEnum start = switch (yearZhi) {
            case YIN, WU, XU -> DiZhiEnum.CHOU;      // 寅午戌 → 丑
            case SHEN, ZI, CHEN -> DiZhiEnum.YIN;    // 申子辰 → 寅
            case SI, YOU, CHOU -> DiZhiEnum.MAO;     // 巳酉丑 → 卯
            case HAI, MAO, WEI -> DiZhiEnum.YOU;     // 亥卯未 → 酉
        };
        return start.moveClockwise(hourZhi.getIndex());
    }

    /**
     * 铃星：按年支+时支定位
     */
    public DiZhiEnum placeLingXing(DiZhiEnum yearZhi, DiZhiEnum hourZhi) {
        DiZhiEnum start = switch (yearZhi) {
            case YIN, WU, XU -> DiZhiEnum.MAO;       // 寅午戌 → 卯
            case SHEN, ZI, CHEN -> DiZhiEnum.XU;     // 申子辰 → 戌
            case SI, YOU, CHOU -> DiZhiEnum.XU;      // 巳酉丑 → 戌
            case HAI, MAO, WEI -> DiZhiEnum.XU;      // 亥卯未 → 戌
        };
        return start.moveClockwise(hourZhi.getIndex());
    }

    /**
     * 擎羊：按年干定位（禄存的前一位 = 擎羊）
     * <p>
     * 擎羊永远在禄存的前一宫（顺时针）
     */
    public DiZhiEnum placeQingYang(TianGanEnum yearGan) {
        AuxiliaryStarPlacer auxPlacer = new AuxiliaryStarPlacer();
        DiZhiEnum luCun = auxPlacer.placeLuCun(yearGan);
        return luCun.moveClockwise(1);
    }

    /**
     * 陀罗：按年干定位（禄存的后一位 = 陀罗）
     * <p>
     * 陀罗永远在禄存的后一宫（逆时针）
     */
    public DiZhiEnum placeTuoLuo(TianGanEnum yearGan) {
        AuxiliaryStarPlacer auxPlacer = new AuxiliaryStarPlacer();
        DiZhiEnum luCun = auxPlacer.placeLuCun(yearGan);
        return luCun.moveCounterClockwise(1);
    }

    /**
     * 地空：按时支定位
     * <p>
     * 子时在亥，逆数至生时
     */
    public DiZhiEnum placeDiKong(DiZhiEnum hourZhi) {
        return DiZhiEnum.HAI.moveCounterClockwise(hourZhi.getIndex());
    }

    /**
     * 地劫：按时支定位
     * <p>
     * 子时在亥，顺数至生时
     */
    public DiZhiEnum placeDiJie(DiZhiEnum hourZhi) {
        return DiZhiEnum.HAI.moveClockwise(hourZhi.getIndex());
    }

    /**
     * 天刑：按农历月定位
     * <p>
     * 正月在酉，顺数至生月
     */
    public DiZhiEnum placeTianXing(int lunarMonth) {
        return DiZhiEnum.YOU.moveClockwise(lunarMonth - 1);
    }

    /**
     * 天姚：按农历月定位
     * <p>
     * 正月在丑，顺数至生月
     */
    public DiZhiEnum placeTianYao(int lunarMonth) {
        return DiZhiEnum.CHOU.moveClockwise(lunarMonth - 1);
    }

    /**
     * 红鸾：按年支定位
     * <p>
     * 子年在卯，逆数至生年支（每支移一位）
     */
    public DiZhiEnum placeHongLuan(DiZhiEnum yearZhi) {
        return DiZhiEnum.MAO.moveCounterClockwise(yearZhi.getIndex());
    }

    /**
     * 天喜：按年支定位（红鸾的对宫）
     * <p>
     * 天喜永远在红鸾的对宫
     */
    public DiZhiEnum placeTianXi(DiZhiEnum yearZhi) {
        return placeHongLuan(yearZhi).opposite();
    }

    /**
     * 创建杂曜落位对象
     */
    public StarPosition createMinorStar(String starCode) {
        String starName = switch (starCode) {
            case "huoxing" -> "火星"; case "lingxing" -> "铃星";
            case "qingyang" -> "擎羊"; case "tuoluo" -> "陀罗";
            case "dikong" -> "地空"; case "dijie" -> "地劫";
            case "tianxing" -> "天刑"; case "tianyao" -> "天姚";
            case "hongluan" -> "红鸾"; case "tianxi" -> "天喜";
            default -> starCode;
        };
        return StarPosition.builder()
                .starCode(starCode)
                .starName(starName)
                .starType(StarTypeEnum.MINOR)
                .build();
    }

}
