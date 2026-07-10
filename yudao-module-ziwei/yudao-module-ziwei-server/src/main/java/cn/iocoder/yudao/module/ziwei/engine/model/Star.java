package cn.iocoder.yudao.module.ziwei.engine.model;

import cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 星曜定义
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Star {

    /** 星曜编码（英文唯一标识） */
    private String code;

    /** 星曜中文名称 */
    private String name;

    /** 星曜类型 */
    private StarTypeEnum type;

    /** 五行属性 */
    private String fiveElement;

    /** 阴阳属性 */
    private String yinYang;

    // ========== 14 主星常量 ==========
    public static final String CODE_ZIWEI = "ziwei";
    public static final String CODE_TIANJI = "tianji";
    public static final String CODE_TAIYANG = "taiyang";
    public static final String CODE_WUQU = "wuqu";
    public static final String CODE_TIANTONG = "tiantong";
    public static final String CODE_LIANZHEN = "lianzhen";
    public static final String CODE_TIANFU = "tianfu";
    public static final String CODE_TAIYIN = "taiyin";
    public static final String CODE_TANLANG = "tanlang";
    public static final String CODE_JUMEN = "jumen";
    public static final String CODE_TIANXIANG = "tianxiang";
    public static final String CODE_TIANLIANG = "tianliang";
    public static final String CODE_QISHA = "qisha";
    public static final String CODE_POJUN = "pojun";

    // 紫微系6星（按位置顺序）
    public static final String[] ZIWEI_GROUP = {CODE_ZIWEI, CODE_TIANJI, CODE_TAIYANG, CODE_WUQU, CODE_TIANTONG, CODE_LIANZHEN};
    // 天府系8星（按位置顺序）
    public static final String[] TIANFU_GROUP = {CODE_TIANFU, CODE_TAIYIN, CODE_TANLANG, CODE_JUMEN, CODE_TIANXIANG, CODE_TIANLIANG, CODE_QISHA, CODE_POJUN};

    // ========== 8 辅星常量 ==========
    public static final String CODE_ZUOFU = "zuofu";
    public static final String CODE_YOUBI = "youbi";
    public static final String CODE_WENCHANG = "wenchang";
    public static final String CODE_WENQU = "wenqu";
    public static final String CODE_TIANKUI = "tiankui";
    public static final String CODE_TIANYUE = "tianyue";
    public static final String CODE_LUCUN = "lucun";
    public static final String CODE_TIANMA = "tianma";

    public static final String[] AUXILIARY_STARS = {CODE_ZUOFU, CODE_YOUBI, CODE_WENCHANG, CODE_WENQU, CODE_TIANKUI, CODE_TIANYUE, CODE_LUCUN, CODE_TIANMA};

    // ========== 杂曜常量 ==========
    public static final String CODE_HUOXING = "huoxing";
    public static final String CODE_LINGXING = "lingxing";
    public static final String CODE_QINGYANG = "qingyang";
    public static final String CODE_TUOLUO = "tuoluo";
    public static final String CODE_DIKONG = "dikong";
    public static final String CODE_DIJIE = "dijie";
    public static final String CODE_TIANXING = "tianxing";
    public static final String CODE_TIANYAO = "tianyao";
    public static final String CODE_HONGLUAN = "hongluan";
    public static final String CODE_TIANXI = "tianxi";

    public static final String[] MINOR_STARS = {CODE_HUOXING, CODE_LINGXING, CODE_QINGYANG, CODE_TUOLUO, CODE_DIKONG, CODE_DIJIE, CODE_TIANXING, CODE_TIANYAO, CODE_HONGLUAN, CODE_TIANXI};

    // ========== 扩展杂曜常量（文墨天机有亮度数据的）==========
    public static final String CODE_TIANFUX = "tianfux";       // 天福（区别于天府 tianfu）
    public static final String CODE_TIANGUAN = "tianguan";     // 天官
    public static final String CODE_BAZUO = "bazuo";           // 八座
    public static final String CODE_SANTAI = "santai";         // 三台
    public static final String CODE_TIANCAI = "tiancai";       // 天才
    public static final String CODE_TIANKONG_M = "tiankong_m"; // 天空（杂曜）
    public static final String CODE_TIANKU = "tianku";         // 天哭
    public static final String CODE_TIANXU = "tianxu";         // 天虚
    public static final String CODE_TIANGUI = "tiangui";       // 天贵
    public static final String CODE_TIANSHOU = "tianshou";     // 天寿
    public static final String CODE_TIANDE_M = "tiande_m";     // 天德（杂曜）
    public static final String CODE_GUCHEN = "guchen";         // 孤辰
    public static final String CODE_XIANCHI = "xianchi";       // 咸池
    public static final String CODE_ENGUANG = "enguang";       // 恩光
    public static final String CODE_POSUI = "posui";           // 破碎
    public static final String CODE_HUAGAI = "huagai";         // 华盖
    public static final String CODE_JIESHEN = "jieshen";       // 解神
    public static final String CODE_GUASU = "guasu";           // 寡宿
    public static final String CODE_FENGGE = "fengge";         // 凤阁
    public static final String CODE_LONGCHI = "longchi";       // 龙池

    // ========== 神煞/流年杂曜 ==========
    public static final String CODE_FENGGAO = "fenggao";       // 封诰
    public static final String CODE_TAIFU_M = "taifu_m";       // 台辅
    public static final String CODE_FEILIAN = "feilian";       // 蜚廉
    public static final String CODE_YINSHA = "yinsha";         // 阴煞
    public static final String CODE_TIANWU = "tianwu";         // 天巫
    public static final String CODE_TIANYUEX = "tianyuex";     // 天月(区别于天钺tianyue)
    public static final String CODE_YUEDE = "yuede";           // 月德
    public static final String CODE_TIANCHU = "tianchu";       // 天厨
    public static final String CODE_NIANJIE = "nianjie";       // 年解
    public static final String CODE_JIESHA = "jiesha";         // 劫煞
    public static final String CODE_LONGDE = "longde";         // 龙德
    public static final String CODE_JIEKONG = "jiekong";       // 截空
    public static final String CODE_XUNKONG = "xunkong";       // 旬空
    public static final String CODE_DAHAO = "dahao";           // 大耗
    public static final String CODE_TIANSHI = "tianshi";       // 天使
    public static final String CODE_TIANSHANG = "tianshang";   // 天伤
    public static final String CODE_FUXUN = "fuxun";           // 副旬
    public static final String CODE_FUJIE = "fujie";           // 副截

    /** 所有扩展杂曜（不含神煞） */
    public static final String[] EXTENDED_MINOR_STARS = {
        CODE_TIANFUX, CODE_TIANGUAN, CODE_BAZUO, CODE_SANTAI, CODE_TIANCAI,
        CODE_TIANKONG_M, CODE_TIANKU, CODE_TIANXU, CODE_TIANGUI, CODE_TIANSHOU,
        CODE_TIANDE_M, CODE_GUCHEN, CODE_XIANCHI, CODE_ENGUANG, CODE_POSUI,
        CODE_HUAGAI, CODE_JIESHEN, CODE_GUASU, CODE_FENGGE, CODE_LONGCHI
    };

    /** 神煞/流年杂曜 */
    public static final String[] SHENSHA_STARS = {
        CODE_FENGGAO, CODE_TAIFU_M, CODE_FEILIAN, CODE_YINSHA, CODE_TIANWU,
        CODE_TIANYUEX, CODE_YUEDE, CODE_TIANCHU, CODE_NIANJIE, CODE_JIESHA,
        CODE_LONGDE, CODE_JIEKONG, CODE_XUNKONG, CODE_DAHAO, CODE_TIANSHI,
        CODE_TIANSHANG, CODE_FUXUN, CODE_FUJIE
    };

    /**
     * 获取星曜中文名（静态工具方法）
     */
    public static String getNameByCode(String code) {
        return switch (code) {
            case CODE_ZIWEI -> "紫微";
            case CODE_TIANJI -> "天机";
            case CODE_TAIYANG -> "太阳";
            case CODE_WUQU -> "武曲";
            case CODE_TIANTONG -> "天同";
            case CODE_LIANZHEN -> "廉贞";
            case CODE_TIANFU -> "天府";
            case CODE_TAIYIN -> "太阴";
            case CODE_TANLANG -> "贪狼";
            case CODE_JUMEN -> "巨门";
            case CODE_TIANXIANG -> "天相";
            case CODE_TIANLIANG -> "天梁";
            case CODE_QISHA -> "七杀";
            case CODE_POJUN -> "破军";
            case CODE_ZUOFU -> "左辅";
            case CODE_YOUBI -> "右弼";
            case CODE_WENCHANG -> "文昌";
            case CODE_WENQU -> "文曲";
            case CODE_TIANKUI -> "天魁";
            case CODE_TIANYUE -> "天钺";
            case CODE_LUCUN -> "禄存";
            case CODE_TIANMA -> "天马";
            case CODE_HUOXING -> "火星";
            case CODE_LINGXING -> "铃星";
            case CODE_QINGYANG -> "擎羊";
            case CODE_TUOLUO -> "陀罗";
            case CODE_DIKONG -> "地空";
            case CODE_DIJIE -> "地劫";
            case CODE_TIANXING -> "天刑";
            case CODE_TIANYAO -> "天姚";
            case CODE_HONGLUAN -> "红鸾";
            case CODE_TIANXI -> "天喜";
            case CODE_TIANFUX -> "天福";
            case CODE_TIANGUAN -> "天官";
            case CODE_BAZUO -> "八座";
            case CODE_SANTAI -> "三台";
            case CODE_TIANCAI -> "天才";
            case CODE_TIANKONG_M -> "天空";
            case CODE_TIANKU -> "天哭";
            case CODE_TIANXU -> "天虚";
            case CODE_TIANGUI -> "天贵";
            case CODE_TIANSHOU -> "天寿";
            case CODE_TIANDE_M -> "天德";
            case CODE_GUCHEN -> "孤辰";
            case CODE_XIANCHI -> "咸池";
            case CODE_ENGUANG -> "恩光";
            case CODE_POSUI -> "破碎";
            case CODE_HUAGAI -> "华盖";
            case CODE_JIESHEN -> "解神";
            case CODE_GUASU -> "寡宿";
            case CODE_FENGGE -> "凤阁";
            case CODE_LONGCHI -> "龙池";
            case CODE_FENGGAO -> "封诰";
            case CODE_TAIFU_M -> "台辅";
            case CODE_FEILIAN -> "蜚廉";
            case CODE_YINSHA -> "阴煞";
            case CODE_TIANWU -> "天巫";
            case CODE_TIANYUEX -> "天月";
            case CODE_YUEDE -> "月德";
            case CODE_TIANCHU -> "天厨";
            case CODE_NIANJIE -> "年解";
            case CODE_JIESHA -> "劫煞";
            case CODE_LONGDE -> "龙德";
            case CODE_JIEKONG -> "截空";
            case CODE_XUNKONG -> "旬空";
            case CODE_DAHAO -> "大耗";
            case CODE_TIANSHI -> "天使";
            case CODE_TIANSHANG -> "天伤";
            case CODE_FUXUN -> "副旬";
            case CODE_FUJIE -> "副截";
            default -> code;
        };
    }

}
