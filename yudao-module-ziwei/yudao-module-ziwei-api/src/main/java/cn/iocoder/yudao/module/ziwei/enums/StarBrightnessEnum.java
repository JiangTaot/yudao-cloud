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

    MIAO(1, "庙", 7),     // 最亮，吉性最强 [+3]
    WANG(2, "旺", 6),     // 次亮 [+2]
    DE(3, "得", 5),       // 得地 [+1]
    LI(4, "利", 4),       // 利益 [0]
    PING(5, "平", 3),     // 中和 [-1]
    XIAN(6, "陷", 1),     // 最暗，凶性最强 [-3]（code=6 保持向后兼容）
    BU(7, "不", 2);       // 不 [-2]（介于平与陷之间，code=7 新增）

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
