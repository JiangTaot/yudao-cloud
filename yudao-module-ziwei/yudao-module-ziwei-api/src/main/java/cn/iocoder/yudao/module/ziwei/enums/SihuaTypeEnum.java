package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 四化类型枚举（化禄、化权、化科、化忌）
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum SihuaTypeEnum {

    HUA_LU(1, "化禄", "禄"),
    HUA_QUAN(2, "化权", "权"),
    HUA_KE(3, "化科", "科"),
    HUA_JI(4, "化忌", "忌");

    private final int code;
    private final String name;
    private final String shortName;

    public static SihuaTypeEnum ofCode(Integer code) {
        if (code == null) return null;
        for (SihuaTypeEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }

}
