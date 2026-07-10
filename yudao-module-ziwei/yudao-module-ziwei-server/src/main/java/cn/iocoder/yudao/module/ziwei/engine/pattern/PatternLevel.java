package cn.iocoder.yudao.module.ziwei.engine.pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 格局等级
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum PatternLevel {

    /** 上格 - 大富贵之格 */
    EXCELLENT("上格", "大富大贵，帝王将相之命"),

    /** 中格 - 中富中贵之格 */
    GOOD("中格", "中富中贵，事业有成之命"),

    /** 平格 - 平常之格 */
    NEUTRAL("平格", "平常之命，但有机遇可成"),

    /** 恶格 - 凶险之格 */
    CAUTION("恶格", "多有波折，需谨慎化解");

    /** 等级名称 */
    private final String name;

    /** 等级说明 */
    private final String description;

}
