package cn.iocoder.yudao.module.ziwei.engine.model;

import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.GenderEnum;
import cn.iocoder.yudao.module.ziwei.enums.PalaceTypeEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 完整命盘上下文（聚合根）
 * <p>
 * 包含八字、十二宫、星曜、四化、大限等全部命盘信息
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartContext {

    // ========== 出生信息 ==========
    /** 公历出生日期时间 */
    private LocalDateTime birthDateTime;

    /** 出生地点 */
    private String birthPlace;

    /** 性别 */
    private GenderEnum gender;

    /** 是否夏令时 */
    private boolean isDst;

    /** 公历年 */
    private int solarYear;
    /** 公历月 */
    private int solarMonth;
    /** 公历日 */
    private int solarDay;
    /** 公历时 */
    private int solarHour;
    /** 公历分 */
    private int solarMinute;

    /** 农历年 */
    private int lunarYear;
    /** 农历月 */
    private int lunarMonth;
    /** 农历日 */
    private int lunarDay;
    /** 是否闰月 */
    private boolean isLeapMonth;

    // ========== 八字 ==========
    private Bazi bazi;
    /** 生年天干 */
    private TianGanEnum yearTianGan;
    /** 生年地支 */
    private DiZhiEnum yearDiZhi;

    // ========== 命盘核心 ==========
    /** 命宫地支 */
    private DiZhiEnum mingGongDiZhi;
    /** 身宫地支 */
    private DiZhiEnum shenGongDiZhi;
    /** 五行局名称（水二局、木三局...） */
    private String wuxingJu;
    /** 五行局数值 2-6 */
    private int wuxingJuNumber;

    // ========== 十二宫 ==========
    /** 十二宫，key=宫位类型 */
    @Builder.Default
    private EnumMap<PalaceTypeEnum, Palace> palaces = new EnumMap<>(PalaceTypeEnum.class);

    // ========== 四化 ==========
    /** 生年四化 */
    @Builder.Default
    private List<StarPosition> natalSihua = new ArrayList<>();

    // ========== 大限 ==========
    /** 大限列表（按顺序排列） */
    @Builder.Default
    private List<DaxianCycle> daxianCycles = new ArrayList<>();

    // ========== 格局 ==========
    /** 匹配到的格局列表（名称） */
    @Builder.Default
    private List<String> patternNames = new ArrayList<>();

    /** 匹配到的格局详情列表 */
    @Builder.Default
    private List<cn.iocoder.yudao.module.ziwei.engine.pattern.PatternResult> patternResults = new ArrayList<>();

    // ========== 便利方法 ==========

    /**
     * 获取指定宫位
     */
    public Palace getPalace(PalaceTypeEnum type) {
        return palaces.get(type);
    }

    /**
     * 获取命宫
     */
    public Palace getMingPalace() {
        return palaces.get(PalaceTypeEnum.MING_GONG);
    }

    /**
     * 查找某颗星落在哪个宫位
     */
    public PalaceTypeEnum findStarPalace(String starCode) {
        for (Map.Entry<PalaceTypeEnum, Palace> entry : palaces.entrySet()) {
            if (entry.getValue().hasStar(starCode)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 获取三方四正的宫位列表（以某宫为中心）
     */
    public List<Palace> getTriadAndOpposite(PalaceTypeEnum center) {
        List<Palace> result = new ArrayList<>();
        PalaceTypeEnum opposite = center.opposite();
        List<PalaceTypeEnum> triad = center.triad();

        result.add(palaces.get(center));       // 本宫
        result.add(palaces.get(opposite));     // 对宫
        for (PalaceTypeEnum t : triad) {       // 三合
            if (t != center) {
                result.add(palaces.get(t));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("ChartContext[%d-%02d-%02d %02d:%02d, %s, 命宫=%s, %s, 格局=%d]",
                solarYear, solarMonth, solarDay, solarHour, solarMinute,
                gender != null ? gender.getName() : "?",
                mingGongDiZhi != null ? mingGongDiZhi.getName() : "?",
                wuxingJu,
                patternNames.size());
    }

}
