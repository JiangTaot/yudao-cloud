package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 天干枚举（甲乙丙丁戊己庚辛壬癸）
 * <p>
 * 用于四化计算、宫干确定、五行局等
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum TianGanEnum {

    JIA(0, "甲", "木", "阳"),
    YI(1, "乙", "木", "阴"),
    BING(2, "丙", "火", "阳"),
    DING(3, "丁", "火", "阴"),
    WU(4, "戊", "土", "阳"),
    JI(5, "己", "土", "阴"),
    GENG(6, "庚", "金", "阳"),
    XIN(7, "辛", "金", "阴"),
    REN(8, "壬", "水", "阳"),
    GUI(9, "癸", "水", "阴");

    public static final List<TianGanEnum> VALUES = Arrays.asList(values());

    /** 序号 0-9 */
    private final int index;
    /** 天干名称 */
    private final String name;
    /** 五行属性 */
    private final String fiveElement;
    /** 阴阳属性 */
    private final String yinYang;

    /**
     * 根据名称获取天干
     */
    public static TianGanEnum ofName(String name) {
        return VALUES.stream()
                .filter(e -> e.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据序号获取天干（0-9）
     */
    public static TianGanEnum ofIndex(int index) {
        return index >= 0 && index < 10 ? VALUES.get(index) : null;
    }

    /**
     * 获取该天干的四化星曜编码（化禄、化权、化科、化忌）
     * <p>
     * 按生年天干确定：
     * 甲: 廉贞化禄 破军化权 武曲化科 太阳化忌
     * 乙: 天机化禄 天梁化权 紫微化科 太阴化忌
     * ...
     */
    public SihuaStars getSihuaStars() {
        return SIHUA_MAP[this.index];
    }

    /**
     * 单个天干的四化星曜
     */
    public record SihuaStars(String huaLu, String huaQuan, String huaKe, String huaJi) {}

    // ========== 四化对照表 ==========
    private static final SihuaStars[] SIHUA_MAP = {
            new SihuaStars("lianzhen", "pojun", "wuqu", "taiyang"),      // 甲
            new SihuaStars("tianji", "tianliang", "ziwei", "taiyin"),     // 乙
            new SihuaStars("tiantong", "tianji", "wenchang", "lianzhen"),  // 丙
            new SihuaStars("taiyin", "tiantong", "tianji", "jumen"),      // 丁
            new SihuaStars("tanlang", "taiyin", "youbi", "tianji"),       // 戊
            new SihuaStars("wuqu", "tanlang", "tianliang", "wenqu"),      // 己
            new SihuaStars("taiyang", "wuqu", "taiyin", "tiantong"),      // 庚
            new SihuaStars("jumen", "taiyang", "wenqu", "wenchang"),      // 辛
            new SihuaStars("tianliang", "ziwei", "zuofu", "wuqu"),        // 壬
            new SihuaStars("pojun", "jumen", "taiyin", "tanlang"),        // 癸
    };

}
