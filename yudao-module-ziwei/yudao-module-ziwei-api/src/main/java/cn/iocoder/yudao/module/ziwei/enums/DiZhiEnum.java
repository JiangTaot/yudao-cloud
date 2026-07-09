package cn.iocoder.yudao.module.ziwei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 地支枚举（子丑寅卯辰巳午未申酉戌亥）
 * <p>
 * 用于命宫定位、十二宫、时辰转换、大限方向等
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum DiZhiEnum {

    ZI(0, "子", "水", "阳", 0, new int[]{23, 0}),      // 子时 23:00-01:00
    CHOU(1, "丑", "土", "阴", 2, new int[]{1, 2}),
    YIN(2, "寅", "木", "阳", 4, new int[]{3, 4}),
    MAO(3, "卯", "木", "阴", 6, new int[]{5, 6}),
    CHEN(4, "辰", "土", "阳", 8, new int[]{7, 8}),
    SI(5, "巳", "火", "阴", 10, new int[]{9, 10}),
    WU(6, "午", "火", "阳", 12, new int[]{11, 12}),
    WEI(7, "未", "土", "阴", 14, new int[]{13, 14}),
    SHEN(8, "申", "金", "阳", 16, new int[]{15, 16}),
    YOU(9, "酉", "金", "阴", 18, new int[]{17, 18}),
    XU(10, "戌", "土", "阳", 20, new int[]{19, 20}),
    HAI(11, "亥", "水", "阴", 22, new int[]{21, 22});

    public static final List<DiZhiEnum> VALUES = Arrays.asList(values());

    /** 序号 0-11 */
    private final int index;
    /** 地支名称 */
    private final String name;
    /** 五行属性 */
    private final String fiveElement;
    /** 阴阳属性 */
    private final String yinYang;
    /** 月建序数（寅=0，正月建寅的对应数） */
    private final int monthIndex;
    /** 对应的小时范围 [startHour, endHour] */
    private final int[] hours;

    /**
     * 根据公历小时(0-23)获取对应的地支（时辰）
     */
    public static DiZhiEnum ofHour(int solarHour) {
        // 特殊处理子时（23点和0点都属子时）
        if (solarHour == 23 || solarHour == 0) return ZI;
        // 其他时辰：每2小时一个地支
        // 1-2=丑, 3-4=寅, ...
        int idx = (solarHour + 1) / 2;
        return VALUES.get(idx);
    }

    /**
     * 获取相对冲的地支（差6位，即180°对宫）
     */
    public DiZhiEnum opposite() {
        return VALUES.get((this.index + 6) % 12);
    }

    /**
     * 获取三合位（顺时针隔4位的地支）
     */
    public List<DiZhiEnum> triad() {
        return Arrays.asList(
                this,
                VALUES.get((this.index + 4) % 12),
                VALUES.get((this.index + 8) % 12)
        );
    }

    /**
     * 顺时针移动 n 位
     */
    public DiZhiEnum moveClockwise(int steps) {
        return VALUES.get((this.index + steps) % 12);
    }

    /**
     * 逆时针移动 n 位（等同于 moveClockwise(12 - steps)）
     */
    public DiZhiEnum moveCounterClockwise(int steps) {
        return VALUES.get((this.index - (steps % 12) + 12) % 12);
    }

    /**
     * 根据名称获取地支
     */
    public static DiZhiEnum ofName(String name) {
        return VALUES.stream().filter(e -> e.name.equals(name)).findFirst().orElse(null);
    }

    /**
     * 根据序号获取地支
     */
    public static DiZhiEnum ofIndex(int index) {
        return index >= 0 && index < 12 ? VALUES.get(index) : null;
    }

}
