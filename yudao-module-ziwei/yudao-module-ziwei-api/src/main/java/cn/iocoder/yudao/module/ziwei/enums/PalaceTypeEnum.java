package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 十二宫位枚举
 * <p>
 * 逆时针排列：命宫→兄弟→夫妻→子女→财帛→疾厄→迁移→交友→官禄→田宅→福德→父母
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum PalaceTypeEnum {

    MING_GONG(1, "命宫", "ming"),
    XIONG_DI(2, "兄弟宫", "xiongdi"),
    FU_QI(3, "夫妻宫", "fuqi"),
    ZI_NV(4, "子女宫", "zinv"),
    CAI_BO(5, "财帛宫", "caibo"),
    JI_E(6, "疾厄宫", "jie"),
    QIAN_YI(7, "迁移宫", "qianyi"),
    JIAO_YOU(8, "交友宫", "jiaoyou"),
    GUAN_LU(9, "官禄宫", "guanlu"),
    TIAN_ZHAI(10, "田宅宫", "tianzhai"),
    FU_DE(11, "福德宫", "fude"),
    FU_MU(12, "父母宫", "fumu");

    public static final List<PalaceTypeEnum> VALUES = Arrays.asList(values());

    /** 宫位编号 1-12 */
    private final int code;
    /** 宫位中文名 */
    private final String name;
    /** 宫位英文缩写（用于编码） */
    private final String alias;

    /**
     * 获取该宫位的对宫（差6位，即180°相对）
     */
    public PalaceTypeEnum opposite() {
        int oppositeCode = (this.code + 5) % 12 + 1;
        return ofCode(oppositeCode);
    }

    /**
     * 获取三合宫位（每隔4位=120°）
     */
    public List<PalaceTypeEnum> triad() {
        return Arrays.asList(
                this,
                ofCode((this.code + 3) % 12 + 1),
                ofCode((this.code + 7) % 12 + 1)
        );
    }

    /**
     * 根据编号获取宫位
     */
    public static PalaceTypeEnum ofCode(int code) {
        return VALUES.stream().filter(e -> e.code == code).findFirst().orElse(null);
    }

    /**
     * 根据名称获取宫位
     */
    public static PalaceTypeEnum ofName(String name) {
        return VALUES.stream().filter(e -> e.name.equals(name)).findFirst().orElse(null);
    }

}
