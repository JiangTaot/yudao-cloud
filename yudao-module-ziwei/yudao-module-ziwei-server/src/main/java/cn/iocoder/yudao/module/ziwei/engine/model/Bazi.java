package cn.iocoder.yudao.module.ziwei.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 八字（四柱）
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bazi {

    /** 年柱 */
    private Pillar yearPillar;

    /** 月柱 */
    private Pillar monthPillar;

    /** 日柱 */
    private Pillar dayPillar;

    /** 时柱 */
    private Pillar hourPillar;

    /**
     * 获取生年天干
     */
    public String getYearTianGan() {
        return yearPillar != null && yearPillar.getTianGan() != null
                ? yearPillar.getTianGan().getName() : null;
    }

    /**
     * 获取生年地支
     */
    public String getYearDiZhi() {
        return yearPillar != null && yearPillar.getDiZhi() != null
                ? yearPillar.getDiZhi().getName() : null;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", yearPillar, monthPillar, dayPillar, hourPillar);
    }

}
