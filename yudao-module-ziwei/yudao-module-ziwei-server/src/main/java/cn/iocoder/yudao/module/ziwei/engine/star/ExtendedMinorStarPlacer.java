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
            DiZhiEnum hourZhi,
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

        // ───── 神煞/流年杂曜 ─────

        // 天厨：年干查表 [甲己→巳,乙→午,丙戊→子,丁→巳,庚→午,辛→申,壬→寅,癸→酉]
        // iztro: ['si','woo','zi','si','woo','shen','yin','woo','you','hai'][stemIndex]
        int[] tianchuTable = {5, 6, 0, 5, 6, 8, 2, 6, 9, 11}; // 巳午子巳午申寅午酉亥
        result.put(Star.CODE_TIANCHU, DiZhiEnum.ofIndex(tianchuTable[ganIdx]));

        // 蜚廉：年支查表
        // iztro: ['shen','you','xu','si','woo','wei','yin','mao','chen','hai','zi','chou'][branchIndex]
        int[] feilianTable = {8, 9, 10, 5, 6, 7, 2, 3, 4, 11, 0, 1};
        result.put(Star.CODE_FEILIAN, DiZhiEnum.ofIndex(feilianTable[yearZhiIdx]));

        // 年解：年支查表（戌起子逆数）
        // iztro: ['xu','you','shen','wei','woo','si','chen','mao','yin','chou','zi','hai'][branchIndex]
        int[] nianjieTable = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 11};
        result.put(Star.CODE_NIANJIE, DiZhiEnum.ofIndex(nianjieTable[yearZhiIdx]));

        // 劫煞：年支三合定 [申子辰→巳,亥卯未→申,寅午戌→亥,巳酉丑→寅]
        DiZhiEnum jiesha = switch (yearDiZhi) {
            case SHEN, ZI, CHEN -> DiZhiEnum.SI;     // 申子辰→巳
            case HAI, MAO, WEI -> DiZhiEnum.SHEN;     // 亥卯未→申
            case YIN, WU, XU -> DiZhiEnum.HAI;        // 寅午戌→亥
            default -> DiZhiEnum.YIN;                  // 巳酉丑→寅
        };
        result.put(Star.CODE_JIESHA, jiesha);

        // 龙德：岁前十二神第8位 → 年支 + 7
        result.put(Star.CODE_LONGDE, yearDiZhi.moveClockwise(7));

        // 月德：从巳起子顺数至年支 → 巳 + yearZhiIdx
        result.put(Star.CODE_YUEDE, DiZhiEnum.SI.moveClockwise(yearZhiIdx));

        // ───── 基于时辰的星曜 ─────
        int timeIdx = hourZhi.getIndex();

        // 封诰：从寅起子时顺数 → 寅 + timeIndex
        result.put(Star.CODE_FENGGAO, DiZhiEnum.YIN.moveClockwise(timeIdx));

        // 台辅：从午起子时顺数 → 午 + timeIndex
        result.put(Star.CODE_TAIFU_M, DiZhiEnum.WU.moveClockwise(timeIdx));

        // ───── 基于生月的星曜（续） ─────

        // 天巫：月支四组 [正五九月巳,二六十月申,三七十一寅,四八十二亥]
        DiZhiEnum tianwu = switch (month0 % 4) {
            case 0 -> DiZhiEnum.SI;    // 正五九
            case 1 -> DiZhiEnum.SHEN;  // 二六十
            case 2 -> DiZhiEnum.YIN;   // 三七十一
            default -> DiZhiEnum.HAI;  // 四八十二
        };
        result.put(Star.CODE_TIANWU, tianwu);

        // 阴煞：月支六组 [正七寅,二八子,三九戌,四十申,五十一午,六十二辰]
        int[] yinshaTable = {2, 0, 10, 8, 6, 4}; // 寅子戌申午辰
        result.put(Star.CODE_YINSHA, DiZhiEnum.ofIndex(yinshaTable[month0 % 6]));

        // 天月：月支12组 [正戌,二巳,三辰,四寅,五未,六卯,七亥,八未,九寅,十午,十一戌,十二寅]
        int[] tianyuexTable = {10, 5, 4, 2, 7, 3, 11, 7, 2, 6, 10, 2};
        result.put(Star.CODE_TIANYUEX, DiZhiEnum.ofIndex(tianyuexTable[month0]));

        // ───── 旬空/截空（iztro 精确天文公式） ─────
        int yearYinYang = yearZhiIdx % 2; // 0=阳, 1=阴

        // 旬空: iztro公式 fixIndex(fbi + 9 - stemIdx) + yinyang调整
        // fbi = fixEarthlyBranchIndex = (branchIdx - 2 + 12) % 12
        int yearIztroIdx = (yearZhiIdx - 2 + 12) % 12;
        int xkIztro = (yearIztroIdx + 9 - ganIdx) % 12;
        if (xkIztro < 0) xkIztro += 12;
        if (yearYinYang != (xkIztro % 2)) xkIztro = (xkIztro + 1) % 12;
        DiZhiEnum xunkong = DiZhiEnum.ofIndex((xkIztro + 2) % 12);
        result.put(Star.CODE_XUNKONG, xunkong);
        result.put(Star.CODE_FUXUN, xunkong.moveClockwise(1));

        // 截空: 截路/空亡表(iztro索引)
        // 截路[申午辰寅子]=[6,4,2,0,10] 空亡[酉未巳卯丑]=[7,5,3,1,11]
        int[] jlIztroTable = {6, 4, 2, 0, 10};
        int[] kwIztroTable = {7, 5, 3, 1, 11};
        int jkIztro;
        if (yearYinYang == 0) {
            jkIztro = jlIztroTable[ganIdx % 5];        // 阳年取截路
        } else {
            jkIztro = (jlIztroTable[ganIdx % 5] - 1 + 12) % 12; // 阴年取截路-1
        }
        DiZhiEnum jiekong = DiZhiEnum.ofIndex((jkIztro + 2) % 12);
        result.put(Star.CODE_JIEKONG, jiekong);
        result.put(Star.CODE_FUJIE, jiekong.moveClockwise(1));

        // ───── 基于命宫的神煞 ─────
        // 天使：疾厄宫(命宫逆5宫)
        result.put(Star.CODE_TIANSHI, mingGongDiZhi.moveCounterClockwise(5));
        // 天伤：交友宫(命宫逆7宫)
        result.put(Star.CODE_TIANSHANG, mingGongDiZhi.moveCounterClockwise(7));

        // 大耗(岁前)：年支对宫，阳顺阴逆一位
        DiZhiEnum duiGong = yearDiZhi.opposite();
        DiZhiEnum dahao = (yearYinYang == 0) ? duiGong.moveClockwise(1) : duiGong.moveCounterClockwise(1);
        result.put(Star.CODE_DAHAO, dahao);

        return result;
    }
}
