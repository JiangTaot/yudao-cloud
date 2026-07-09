package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 五行局枚举
 * <p>
 * 由命宫天干地支查六十纳音表决定，用于紫微星定位和大限起始岁数
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum WuxingJuEnum {

    SHUI_ER_JU("水二局", "水", 2),
    MU_SAN_JU("木三局", "木", 3),
    JIN_SI_JU("金四局", "金", 4),
    TU_WU_JU("土五局", "土", 5),
    HUO_LIU_JU("火六局", "火", 6);

    private final String name;
    private final String fiveElement;
    /** 大限起始岁数 = 局数 */
    private final int number;

    public static WuxingJuEnum ofElement(String fiveElement) {
        for (WuxingJuEnum e : values()) {
            if (e.fiveElement.equals(fiveElement)) return e;
        }
        return null;
    }

    public static WuxingJuEnum ofNumber(int number) {
        for (WuxingJuEnum e : values()) {
            if (e.number == number) return e;
        }
        return null;
    }

}
