package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.engine.model.Star;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.GenderEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 扩展杂曜安放（文墨天机数据，参考 iztro location.js）
 * <p>
 * 安放 27 颗扩展杂曜，部分依赖其他星曜位置（三台→左辅、恩光→文昌等）。
 *
 * @author JTWORLD
 */
public class ExtendedMinorStarPlacer {

    /** 博士十二神顺序（从禄存起） */
    private static final String[] BOSHI_12 = {
        "boshi", "lishi", "qinglong", "xiaohao", "jiangjun", "zoushu",
        "feilian", "xishen", "bingfu", "dahao", "fubing", "guanfu"
    };

    // 天官天福表（年干→地支索引）
    private static final int[] TIANGUAN_TABLE = {7, 4, 5, 2, 3, 9, 11, 9, 10, 6}; // 未辰巳寅卯酉亥酉戌午
    private static final int[] TIANFU_TABLE  = {9, 8, 0, 11, 3, 2, 6, 5, 6, 5};     // 酉申子亥卯寅午巳午巳

    /**
     * 安放所有扩展杂曜
     *
     * @param lunarMonth   农历月 (1-12)
     * @param lunarDay     农历日 (1-30)
     * @param yearDiZhi    年支
     * @param yearTianGan  年干
     * @param gender       性别
     * @param mingGongDiZhi 命宫地支
     * @param shenGongDiZhi 身宫地支
     * @param zuoFuDiZhi   左辅所在地支
     * @param youBiDiZhi   右弼所在地支
     * @param wenChangDiZhi 文昌所在地支
     * @param wenQuDiZhi   文曲所在地支
     * @param luCunDiZhi   禄存所在地支
     * @return 星曜编码→所在地支
     */
    public Map<String, DiZhiEnum> placeAll(
            int lunarMonth, int lunarDay,
            DiZhiEnum yearDiZhi, TianGanEnum yearTianGan,
            GenderEnum gender,
            DiZhiEnum mingGongDiZhi, DiZhiEnum shenGongDiZhi,
            DiZhiEnum zuoFuDiZhi, DiZhiEnum youBiDiZhi,
            DiZhiEnum wenChangDiZhi, DiZhiEnum wenQuDiZhi,
            DiZhiEnum luCunDiZhi) {

        Map<String, DiZhiEnum> result = new LinkedHashMap<>();

        // ───── 基于生日的星曜 ─────
        int dayIdx = lunarDay - 1; // 初一=0
        // 三台：左辅起初一，顺行至生日
        result.put(Star.CODE_SANTAI, zuoFuDiZhi.moveClockwise(dayIdx));
        // 八座：右弼起初一，逆行至生日
        result.put(Star.CODE_BAZUO, youBiDiZhi.moveCounterClockwise(dayIdx));
        // 恩光：文昌起初一，顺行至生日，再退一宫
        result.put(Star.CODE_ENGUANG, wenChangDiZhi.moveClockwise(dayIdx).moveCounterClockwise(1));
        // 天贵：文曲起初一，顺行至生日，再退一宫
        result.put(Star.CODE_TIANGUI, wenQuDiZhi.moveClockwise(dayIdx).moveCounterClockwise(1));

        // ───── 基于命宫/身宫 + 年支 ─────
        int yearZhiIdx = yearDiZhi.getIndex();
        // 天才：命宫起子，顺行至生年支
        result.put(Star.CODE_TIANCAI, mingGongDiZhi.moveClockwise(yearZhiIdx));
        // 天寿：身宫起子，顺行至生年支
        result.put(Star.CODE_TIANSHOU, shenGongDiZhi.moveClockwise(yearZhiIdx));

        // ───── 基于年支 ─────
        // 天空(杂曜)：年支顺数前一位
        result.put(Star.CODE_TIANKONG_M, yearDiZhi.moveClockwise(1));

        // 天哭：午宫起子，逆数至生年支
        result.put(Star.CODE_TIANKU, DiZhiEnum.WU.moveCounterClockwise(yearZhiIdx));

        // 天虚：午宫起子，顺数至生年支
        result.put(Star.CODE_TIANXU, DiZhiEnum.WU.moveClockwise(yearZhiIdx));

        // 天德(杂曜)：酉宫起子，顺数至生年支
        result.put(Star.CODE_TIANDE_M, DiZhiEnum.YOU.moveClockwise(yearZhiIdx));

        // 龙池：辰宫起子，顺行至生年支
        result.put(Star.CODE_LONGCHI, DiZhiEnum.CHEN.moveClockwise(yearZhiIdx));

        // 凤阁：戌宫起子，逆行至生年支
        result.put(Star.CODE_FENGGE, DiZhiEnum.XU.moveCounterClockwise(yearZhiIdx));

        // ───── 基于年支三合局 ─────
        // 咸池、华盖
        DiZhiEnum triadKey = yearDiZhi; // 用于switch
        DiZhiEnum xianchi = switch (triadKey) {
            case YIN, WU, XU -> DiZhiEnum.MAO;      // 寅午戌→卯
            case SHEN, ZI, CHEN -> DiZhiEnum.YOU;    // 申子辰→酉
            case SI, YOU, CHOU -> DiZhiEnum.WU;      // 巳酉丑→午
            default -> DiZhiEnum.ZI;                  // 亥卯未→子
        };
        result.put(Star.CODE_XIANCHI, xianchi);

        DiZhiEnum huagai = switch (triadKey) {
            case YIN, WU, XU -> DiZhiEnum.XU;        // 寅午戌→戌
            case SHEN, ZI, CHEN -> DiZhiEnum.CHEN;    // 申子辰→辰
            case SI, YOU, CHOU -> DiZhiEnum.CHOU;     // 巳酉丑→丑
            default -> DiZhiEnum.WEI;                  // 亥卯未→未
        };
        result.put(Star.CODE_HUAGAI, huagai);

        // 孤辰：寅卯辰→巳, 巳午未→申, 申酉戌→亥, 亥子丑→寅
        DiZhiEnum guchen = switch (triadKey) {
            case YIN, MAO, CHEN -> DiZhiEnum.SI;
            case SI, WU, WEI -> DiZhiEnum.SHEN;
            case SHEN, YOU, XU -> DiZhiEnum.HAI;
            default -> DiZhiEnum.YIN;                  // 亥子丑→寅
        };
        result.put(Star.CODE_GUCHEN, guchen);

        // 寡宿：寅卯辰→丑, 巳午未→辰, 申酉戌→未, 亥子丑→戌
        DiZhiEnum guasu = switch (triadKey) {
            case YIN, MAO, CHEN -> DiZhiEnum.CHOU;
            case SI, WU, WEI -> DiZhiEnum.CHEN;
            case SHEN, YOU, XU -> DiZhiEnum.WEI;
            default -> DiZhiEnum.XU;                   // 亥子丑→戌
        };
        result.put(Star.CODE_GUASU, guasu);

        // 破碎：iztro公式 ['si','chou','you'][branchIndex % 3]
        // 子丑寅卯辰巳午未申酉戌亥 → index%3: 0→巳,1→丑,2→酉 循环
        DiZhiEnum posui = switch (yearZhiIdx % 3) {
            case 0 -> DiZhiEnum.SI;
            case 1 -> DiZhiEnum.CHOU;
            default -> DiZhiEnum.YOU;
        };
        result.put(Star.CODE_POSUI, posui);

        // ───── 基于年干 ─────
        int ganIdx = yearTianGan.getIndex();
        // 天官
        result.put(Star.CODE_TIANGUAN, DiZhiEnum.ofIndex(TIANGUAN_TABLE[ganIdx]));
        // 天福
        result.put(Star.CODE_TIANFUX, DiZhiEnum.ofIndex(TIANFU_TABLE[ganIdx]));

        // ───── 基于生月 ─────
        // 解神(月解)：正二申、三四戌、五六子、七八寅、九十辰、十一十二午
        int month0 = lunarMonth - 1; // 0-based
        DiZhiEnum jieshen = switch (month0 / 2) {
            case 0 -> DiZhiEnum.SHEN;  // 正二→申
            case 1 -> DiZhiEnum.XU;    // 三四→戌
            case 2 -> DiZhiEnum.ZI;    // 五六→子
            case 3 -> DiZhiEnum.YIN;   // 七八→寅
            case 4 -> DiZhiEnum.CHEN;  // 九十→辰
            default -> DiZhiEnum.WU;   // 十一十二→午
        };
        result.put(Star.CODE_JIESHEN, jieshen);

        return result;
    }
}
