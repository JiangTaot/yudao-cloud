package cn.iocoder.yudao.module.ziwei.engine.model;

import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 柱（天干+地支组合）
 * <p>
 * 用于表示年柱、月柱、日柱、时柱
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pillar {

    /** 天干 */
    private TianGanEnum tianGan;

    /** 地支 */
    private DiZhiEnum diZhi;

    /**
     * 格式化为 "甲子" 等形式的字符串
     */
    @Override
    public String toString() {
        if (tianGan == null || diZhi == null) return "";
        return tianGan.getName() + diZhi.getName();
    }

    /**
     * 通过字符串创建（如 "甲子"）
     */
    public static Pillar of(String s) {
        if (s == null || s.length() < 2) return null;
        TianGanEnum tg = TianGanEnum.ofName(s.substring(0, 1));
        DiZhiEnum dz = DiZhiEnum.ofName(s.substring(1));
        if (tg == null || dz == null) return null;
        return new Pillar(tg, dz);
    }

    /**
     * 获取纳音五行（六十纳音）
     */
    public String getNaYin() {
        return NaYinData.get(tianGan, diZhi);
    }

}
