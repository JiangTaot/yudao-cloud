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
            default -> code;
        };
    }

}
