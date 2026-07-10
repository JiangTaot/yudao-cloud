package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.engine.model.Star;
import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarBrightnessEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 星曜亮度计算器
 * <p>
 * 亮度数据来源：文墨天机 QS（全书）体系，基于《紫微斗数全书》。
 * 覆盖 48 颗星曜：14主星 + 8辅星 + 10杂曜 + 20扩展杂曜（不含神煞）。
 * <p>
 * 亮度等级（7级）：庙(MIAO) > 旺(WANG) > 得(DE) > 利(LI) > 平(PING) > 不(BU) > 陷(XIAN)
 * 编码：1=庙, 2=旺, 3=得, 4=利, 5=平, 6=不, 7=陷, 0=无(返回 null)
 * <p>
 * 地支顺序（文墨天机原生）：子(0), 丑(1), 寅(2), 卯(3), 辰(4), 巳(5), 午(6), 未(7), 申(8), 酉(9), 戌(10), 亥(11)
 *
 * @author JTWORLD
 */
public class BrightnessCalculator {

    /** 文墨天机 QS 亮度编码 → 枚举映射（0=无亮度返回null） */
    private static final StarBrightnessEnum[] CODE_MAP = {
        null,                      // 0 = 无
        StarBrightnessEnum.MIAO,   // 1 = 庙
        StarBrightnessEnum.WANG,   // 2 = 旺
        StarBrightnessEnum.DE,     // 3 = 得
        StarBrightnessEnum.LI,     // 4 = 利
        StarBrightnessEnum.PING,   // 5 = 平
        StarBrightnessEnum.BU,     // 6 = 不
        StarBrightnessEnum.XIAN,   // 7 = 陷
    };

    /**
     * 亮度表：starCode → 12宫的文墨天机 QS 编码数组（顺序：子丑寅卯辰巳午未申酉戌亥）
     */
    private static final Map<String, int[]> TABLE = new HashMap<>();

    static {
        // ══════════════════ 十四主星 ══════════════════

        // 紫微
        TABLE.put(Star.CODE_ZIWEI,    arr(5,1,2,2,3,2,1,1,2,2,3,2));

        // 天机
        TABLE.put(Star.CODE_TIANJI,   arr(1,7,3,2,4,5,1,7,3,2,4,5));

        // 太阳
        TABLE.put(Star.CODE_TAIYANG,  arr(7,6,2,1,2,2,2,3,3,5,6,7));

        // 武曲
        TABLE.put(Star.CODE_WUQU,     arr(2,1,3,4,1,5,2,1,3,4,1,5));

        // 天同
        TABLE.put(Star.CODE_TIANTONG, arr(2,6,4,5,5,1,7,6,2,5,5,1));

        // 廉贞
        TABLE.put(Star.CODE_LIANZHEN, arr(5,4,1,5,4,7,5,4,1,5,4,7));

        // 天府
        TABLE.put(Star.CODE_TIANFU,   arr(1,1,1,3,1,3,2,1,3,2,1,3));

        // 太阴
        TABLE.put(Star.CODE_TAIYIN,   arr(1,1,2,7,7,7,6,6,4,2,2,1));

        // 贪狼
        TABLE.put(Star.CODE_TANLANG,  arr(2,1,5,4,1,7,2,1,5,4,1,7));

        // 巨门
        TABLE.put(Star.CODE_JUMEN,    arr(2,6,1,1,7,2,2,6,1,1,7,2));

        // 天相
        TABLE.put(Star.CODE_TIANXIANG,arr(1,1,1,7,3,3,1,3,1,7,3,3));

        // 天梁
        TABLE.put(Star.CODE_TIANLIANG,arr(1,2,1,1,1,3,1,2,7,3,1,7));

        // 七杀
        TABLE.put(Star.CODE_QISHA,    arr(2,1,1,2,1,5,2,1,1,2,1,5));

        // 破军
        TABLE.put(Star.CODE_POJUN,    arr(1,2,3,7,2,5,1,2,3,7,2,5));

        // ══════════════════ 八辅星 ══════════════════

        // 文昌
        TABLE.put(Star.CODE_WENCHANG, arr(3,1,7,4,3,1,7,4,3,1,7,4));

        // 文曲
        TABLE.put(Star.CODE_WENQU,    arr(3,1,5,2,3,1,7,2,3,1,7,2));

        // 左辅
        TABLE.put(Star.CODE_ZUOFU,    arr(2,1,1,7,1,5,2,1,5,7,1,6));

        // 右弼
        TABLE.put(Star.CODE_YOUBI,    arr(1,1,2,7,1,5,2,1,6,7,1,5));

        // 天魁
        TABLE.put(Star.CODE_TIANKUI,  arr(2,2,0,1,0,0,1,0,0,0,0,2));

        // 天钺
        TABLE.put(Star.CODE_TIANYUE,  arr(0,0,2,0,0,2,0,2,1,1,0,0));

        // 禄存
        TABLE.put(Star.CODE_LUCUN,    arr(1,0,1,1,0,1,1,0,1,1,0,1));

        // 天马
        TABLE.put(Star.CODE_TIANMA,   arr(0,0,2,0,0,5,0,0,2,0,0,5));

        // ══════════════════ 十杂曜 ══════════════════

        // 火星
        TABLE.put(Star.CODE_HUOXING,  arr(7,3,1,4,7,3,1,4,7,3,1,4));

        // 铃星
        TABLE.put(Star.CODE_LINGXING, arr(7,3,1,4,7,3,1,4,7,3,1,4));

        // 擎羊
        TABLE.put(Star.CODE_QINGYANG, arr(7,1,0,7,1,0,7,1,0,7,1,0));

        // 陀罗
        TABLE.put(Star.CODE_TUOLUO,   arr(0,1,7,0,1,7,0,1,7,0,1,7));

        // 地空
        TABLE.put(Star.CODE_DIKONG,   arr(5,7,7,5,7,1,1,5,1,1,7,7));

        // 地劫
        TABLE.put(Star.CODE_DIJIE,    arr(7,7,5,5,7,6,1,5,1,5,5,7));

        // 天刑
        TABLE.put(Star.CODE_TIANXING, arr(5,7,1,1,5,7,5,7,7,1,1,7));

        // 天姚
        TABLE.put(Star.CODE_TIANYAO,  arr(7,5,2,1,7,5,5,2,7,1,1,7));

        // 红鸾
        TABLE.put(Star.CODE_HONGLUAN, arr(1,7,2,1,1,2,2,7,1,2,7,1));

        // 天喜
        TABLE.put(Star.CODE_TIANXI,   arr(2,7,1,2,7,1,1,7,2,1,7,2));

        // ══════════════════ 扩展杂曜（文墨天机 QS）══════════════════

        // 天福
        TABLE.put(Star.CODE_TIANFUX,    arr(5,0,2,5,0,2,5,0,1,1,0,1));

        // 天官
        TABLE.put(Star.CODE_TIANGUAN,   arr(0,0,5,2,2,2,1,1,0,5,5,2));

        // 八座
        TABLE.put(Star.CODE_BAZUO,      arr(7,1,1,5,2,1,2,5,1,1,5,1));

        // 三台
        TABLE.put(Star.CODE_SANTAI,     arr(5,1,5,7,1,5,2,1,2,1,2,5));

        // 天才
        TABLE.put(Star.CODE_TIANCAI,    arr(2,5,1,2,7,1,2,5,1,2,7,1));

        // 天空（杂曜）
        TABLE.put(Star.CODE_TIANKONG_M, arr(7,5,7,5,1,1,1,7,2,2,7,5));

        // 天哭
        TABLE.put(Star.CODE_TIANKU,     arr(5,1,5,1,5,6,7,5,1,6,5,5));

        // 天虚
        TABLE.put(Star.CODE_TIANXU,     arr(7,1,2,1,7,2,5,7,1,2,7,5));

        // 天贵
        TABLE.put(Star.CODE_TIANGUI,    arr(1,2,5,2,2,5,1,2,7,1,2,5));

        // 天寿
        TABLE.put(Star.CODE_TIANSHOU,   arr(5,1,2,7,1,5,5,2,2,5,1,2));

        // 天德（杂曜）
        TABLE.put(Star.CODE_TIANDE_M,   arr(1,1,5,5,1,2,2,1,5,6,1,5));

        // 孤辰
        TABLE.put(Star.CODE_GUCHEN,     arr(0,0,5,0,0,7,0,0,5,0,0,7));

        // 咸池
        TABLE.put(Star.CODE_XIANCHI,    arr(7,0,0,5,0,0,7,0,0,5,0,0));

        // 恩光
        TABLE.put(Star.CODE_ENGUANG,    arr(5,1,5,1,1,5,1,2,5,7,1,6));

        // 破碎
        TABLE.put(Star.CODE_POSUI,      arr(0,7,0,0,0,7,0,0,0,5,0,0));

        // 华盖
        TABLE.put(Star.CODE_HUAGAI,     arr(0,7,0,0,1,0,0,7,0,0,5,0));

        // 解神
        TABLE.put(Star.CODE_JIESHEN,    arr(1,5,1,1,1,2,1,5,6,2,1,5));

        // 寡宿
        TABLE.put(Star.CODE_GUASU,      arr(0,5,0,0,7,0,0,6,0,0,7,0));

        // 凤阁
        TABLE.put(Star.CODE_FENGGE,     arr(1,5,1,2,7,1,5,7,6,1,1,2));

        // 龙池
        TABLE.put(Star.CODE_LONGCHI,    arr(2,5,5,1,1,7,6,1,5,1,7,2));

        // ══════════════════ 神煞/流年杂曜 ══════════════════

        // 旬空
        TABLE.put(Star.CODE_XUNKONG,    arr(7,0,0,5,0,0,7,0,0,5,0,0));

        // 大耗
        TABLE.put(Star.CODE_DAHAO,      arr(2,5,7,6,5,7,2,5,7,6,5,7));

        // 天使
        TABLE.put(Star.CODE_TIANSHI,    arr(7,7,5,5,7,5,5,5,5,7,7,2));

        // 天伤
        TABLE.put(Star.CODE_TIANSHANG,  arr(7,5,5,7,5,5,7,7,5,5,5,2));

        // 以下星曜无 QS 亮度数据（全0），仅占位
        // 封诰/台辅/蜚廉/阴煞/天巫/天月/月德/天厨/年解/劫煞/龙德/截空/副旬/副截
    }

    /** 便捷写法：创建 int 数组 */
    private static int[] arr(int... values) {
        return values;
    }

    /**
     * 计算星曜在指定地支的亮度（文墨天机 QS 体系）
     * <p>
     * 地支索引直接对应文墨天机顺序：0=子, 1=丑, ..., 11=亥。
     * 编码 0（无亮度）返回 null。
     *
     * @param starCode 星曜编码
     * @param branch   所在地支
     * @return 亮度等级；无数据或编码为0则返回 null
     */
    public StarBrightnessEnum calculate(String starCode, DiZhiEnum branch) {
        if (starCode == null || branch == null) return null;
        int[] table = TABLE.get(starCode);
        if (table == null) return null;
        int code = table[branch.getIndex()];
        if (code == 0) return null;
        return CODE_MAP[code];
    }

    /** 判断星曜是否庙旺 */
    public boolean isBright(String starCode, DiZhiEnum branch) {
        StarBrightnessEnum b = calculate(starCode, branch);
        return b == StarBrightnessEnum.MIAO || b == StarBrightnessEnum.WANG;
    }

    /** 判断星曜是否落陷 */
    public boolean isDim(String starCode, DiZhiEnum branch) {
        StarBrightnessEnum b = calculate(starCode, branch);
        return b == StarBrightnessEnum.XIAN;
    }

}
