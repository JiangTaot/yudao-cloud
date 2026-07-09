package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阴阳枚举
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum YinYangEnum {

    YIN(0, "阴"),
    YANG(1, "阳");

    private final int code;
    private final String name;

    public static YinYangEnum ofCode(Integer code) {
        if (code == null) return null;
        return code == 1 ? YANG : YIN;
    }

}
