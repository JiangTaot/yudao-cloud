package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 五行枚举（金木水火土）
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum FiveElementEnum {

    METAL(0, "金", 4),
    WOOD(1, "木", 3),
    WATER(2, "水", 2),
    FIRE(3, "火", 6),
    EARTH(4, "土", 5);

    private final int code;
    private final String name;
    /** 五行局数值（用于紫微星定位和大限起始岁数） */
    private final int juNumber;

    public static FiveElementEnum ofName(String name) {
        for (FiveElementEnum e : values()) {
            if (e.name.equals(name)) return e;
        }
        return null;
    }

}
