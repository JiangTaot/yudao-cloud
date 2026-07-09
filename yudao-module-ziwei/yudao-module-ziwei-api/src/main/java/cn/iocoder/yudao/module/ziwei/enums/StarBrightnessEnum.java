package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 星曜亮度枚举（庙旺得利平陷）
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum StarBrightnessEnum {

    MIAO(1, "庙", 6),     // 最亮，吉性最强
    WANG(2, "旺", 5),     // 次亮
    DE(3, "得", 4),       // 得地
    LI(4, "利", 3),       // 利益
    PING(5, "平", 2),     // 中和
    XIAN(6, "陷", 1);     // 最暗，凶性最强

    private final int code;
    private final String name;
    /** 亮度等级 1-6，越大越亮 */
    private final int level;

    public static StarBrightnessEnum ofCode(Integer code) {
        if (code == null) return null;
        for (StarBrightnessEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }

}
