package cn.iocoder.yudao.module.ziwei.engine.model;

import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.PalaceTypeEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流年计算结果
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiuninResult {

    /** 流年年份 */
    private int liunianYear;

    /** 流年天干 */
    private TianGanEnum liunianGan;

    /** 流年地支 */
    private DiZhiEnum liunianZhi;

    /** 流年命宫类型 */
    private PalaceTypeEnum liunianMingPalaceType;

    /** 流年命宫名称 */
    private String liunianMingPalaceName;

    /** 流年四化（4个星曜） */
    @Builder.Default
    private List<StarPosition> liunianSihua = new ArrayList<>();

    /** 流年星曜位置：starCode -> 所在地支 */
    @Builder.Default
    private Map<String, DiZhiEnum> liunianStarPositions = new HashMap<>();

    /** 流年星曜落位详情：starCode -> StarPosition（含亮度信息） */
    @Builder.Default
    private Map<String, StarPosition> liunianStarDetails = new HashMap<>();

}
