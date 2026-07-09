package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {

    FEMALE(0, "女", false),
    MALE(1, "男", true);

    private final int code;
    private final String name;
    /** 是否为阳性（男=阳, 女=阴） */
    private final boolean isYang;

    public static GenderEnum ofCode(Integer code) {
        if (code == null) return null;
        return code == 1 ? MALE : FEMALE;
    }

}
