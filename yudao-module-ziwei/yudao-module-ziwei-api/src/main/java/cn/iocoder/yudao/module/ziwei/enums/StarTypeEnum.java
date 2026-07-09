package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 星曜类型枚举
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum StarTypeEnum {

    MAJOR(1, "主星"),       // 14 主星
    AUXILIARY(2, "辅星"),   // 左辅右弼文昌文曲天魁天钺禄存天马
    MINOR(3, "杂曜"),       // 火星铃星擎羊陀罗地空地劫天刑天姚红鸾天喜...
    TRANSFORM(4, "化星");   // 四化星

    private final int code;
    private final String name;

    public static StarTypeEnum ofCode(Integer code) {
        if (code == null) return null;
        for (StarTypeEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }

}
