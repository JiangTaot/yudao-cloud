package cn.iocoder.yudao.module.ziwei.engine.pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 格局匹配结果
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatternResult {

    /** 格局名称 */
    private String name;

    /** 格局等级 */
    private PatternLevel level;

    /** 格局描述 */
    private String description;

    /** 古籍出处 */
    private String source;

    /** 必须满足的条件 */
    @Builder.Default
    private List<String> required = new ArrayList<>();

    /** 加分条件 */
    @Builder.Default
    private List<String> bonus = new ArrayList<>();

    /** 破格条件 */
    @Builder.Default
    private List<String> breaking = new ArrayList<>();

    /** 涉及的宫位 */
    @Builder.Default
    private List<String> involvedPalaces = new ArrayList<>();

}
