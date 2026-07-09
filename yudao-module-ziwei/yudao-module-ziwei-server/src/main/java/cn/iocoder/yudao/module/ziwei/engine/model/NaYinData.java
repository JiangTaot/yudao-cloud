package cn.iocoder.yudao.module.ziwei.engine.model;

import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;

/**
 * 六十纳音数据
 * <p>
 * 用于五行局计算：根据天干+地支组合查对应五行
 *
 * @author JTWORLD
 */
public class NaYinData {

    /**
     * 六十纳音表，按天干+地支组合返回五行名称
     * <p>
     * 甲子乙丑海中金，丙寅丁卯炉中火，戊辰己巳大林木，庚午辛未路旁土，壬申癸酉剑锋金...
     * 每相邻两组的五行相同
     */
    private static final String[][] TABLE = {
            // 甲子 乙丑 丙寅 丁卯  戊辰 己巳  庚午 辛未  壬申 癸酉
            {"金", "金", "火", "火", "木", "木", "土", "土", "金", "金"}, // 甲~癸 (年干=甲)
            // 甲戌 乙亥 丙子 丁丑  戊寅 己卯  庚辰 辛巳  壬午 癸未
            {"火", "火", "水", "水", "土", "土", "金", "金", "木", "木"}, // 甲~癸 (年干=甲)
            // 甲申 乙酉 丙戌 丁亥  戊子 己丑  庚寅 辛卯  壬辰 癸巳
            {"水", "水", "土", "土", "火", "火", "木", "木", "水", "水"}, // 甲~癸 (年干=甲)
            // 甲午 乙未 丙申 丁酉  戊戌 己亥  庚子 辛丑  壬寅 癸卯
            {"金", "金", "火", "火", "木", "木", "土", "土", "金", "金"}, // (年干=甲午)
            // 甲辰 乙巳 丙午 丁未  戊申 己酉  庚戌 辛亥  壬子 癸丑
            {"火", "火", "水", "水", "土", "土", "金", "金", "木", "木"}, // (年干=甲辰)
            // 甲寅 乙卯 丙辰 丁巳  戊午 己未  庚申 辛酉  壬戌 癸亥
            {"水", "水", "土", "土", "火", "火", "木", "木", "水", "水"}, // (年干=甲寅)
    };

    /**
     * 根据天干+地支获取纳音五行
     */
    public static String get(TianGanEnum tianGan, DiZhiEnum diZhi) {
        if (tianGan == null || diZhi == null) return null;
        int row = diZhi.getIndex() / 2;           // 0-5, 每两行为一组
        int col = tianGan.getIndex();             // 0-9
        return TABLE[row][col];
    }

    /**
     * 根据天干+地支获取五行局
     * <p>
     * 五行局直接用纳音五行对应：
     * 金 → 金四局, 木 → 木三局, 水 → 水二局, 火 → 火六局, 土 → 土五局
     */
    public static int getWuxingJuNumber(TianGanEnum tianGan, DiZhiEnum diZhi) {
        String wuxing = get(tianGan, diZhi);
        return switch (wuxing) {
            case "水" -> 2;
            case "木" -> 3;
            case "金" -> 4;
            case "土" -> 5;
            case "火" -> 6;
            default -> 0;
        };
    }

    /**
     * 根据天干+地支获取五行局名称
     */
    public static String getWuxingJuName(TianGanEnum tianGan, DiZhiEnum diZhi) {
        int num = getWuxingJuNumber(tianGan, diZhi);
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
