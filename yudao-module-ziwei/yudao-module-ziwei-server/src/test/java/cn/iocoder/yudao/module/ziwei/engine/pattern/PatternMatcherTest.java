package cn.iocoder.yudao.module.ziwei.engine.pattern;

import cn.iocoder.yudao.module.ziwei.engine.model.*;
import cn.iocoder.yudao.module.ziwei.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PatternMatcher 单元测试
 *
 * @author JTWORLD
 */
public class PatternMatcherTest {

    private PatternMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new PatternMatcher();
    }

    /**
     * 测试紫微朝垣格：紫微在命宫
     */
    @Test
    void testZiWeiChaoYuan() {
        ChartContext ctx = createChartWithStarInMingPalace("ziwei");
        List<PatternResult> results = matcher.matchPatternsDetailed(ctx);
        assertTrue(results.stream().anyMatch(r -> r.getName().contains("紫微朝垣")));
    }

    /**
     * 测试命无正曜格：命宫无主星
     */
    @Test
    void testMingWuZhengYao() {
        ChartContext ctx = createEmptyChart();
        List<PatternResult> results = matcher.matchPatternsDetailed(ctx);
        assertTrue(results.stream().anyMatch(r -> r.getName().contains("命无正曜")));
    }

    /**
     * 测试紫微入命格
     */
    @Test
    void testZiWeiInMing() {
        ChartContext ctx = createChartWithStarInMingPalace("ziwei");
        List<PatternResult> results = matcher.matchPatternsDetailed(ctx);
        assertTrue(results.stream().anyMatch(r -> r.getName().contains("紫微入命")));
    }

    /**
     * 测试武曲守垣格
     */
    @Test
    void testWuQuShouYuan() {
        ChartContext ctx = createChartWithStarInMingPalace("wuqu");
        List<PatternResult> results = matcher.matchPatternsDetailed(ctx);
        assertTrue(results.stream().anyMatch(r -> r.getName().contains("武曲守垣")));
    }

    /**
     * 测试日照雷门格：太阳在卯宫
     */
    @Test
    void testRiZhaoLeiMen() {
        ChartContext ctx = createChartWithStarAtDiZhi("taiyang", DiZhiEnum.MAO);
        List<PatternResult> results = matcher.matchPatternsDetailed(ctx);
        assertTrue(results.stream().anyMatch(r -> r.getName().contains("日照雷门")));
    }

    /**
     * 测试月朗天门格：太阴在亥宫
     */
    @Test
    void testYueLangTianMen() {
        ChartContext ctx = createChartWithStarAtDiZhi("taiyin", DiZhiEnum.HAI);
        List<PatternResult> results = matcher.matchPatternsDetailed(ctx);
        assertTrue(results.stream().anyMatch(r -> r.getName().contains("月朗天门")));
    }

    /**
     * 测试空命盘不应该匹配任何格局
     */
    @Test
    void testEmptyChartHasNoUpperPatterns() {
        ChartContext ctx = createEmptyChart();
        List<PatternResult> results = matcher.matchPatternsDetailed(ctx);
        long upperCount = results.stream()
                .filter(r -> r.getLevel() == PatternLevel.EXCELLENT || r.getLevel() == PatternLevel.GOOD)
                .count();
        assertEquals(0, upperCount, "空命盘不应匹配上格/中格");
    }

    /**
     * 测试 matchPatterns() 兼容接口
     */
    @Test
    void testMatchPatternsCompatibility() {
        ChartContext ctx = createChartWithStarInMingPalace("ziwei");
        List<String> names = matcher.matchPatterns(ctx);
        assertNotNull(names);
        assertFalse(names.isEmpty(), "应该有匹配的格局");
    }

    // ========== 辅助方法 ==========

    private ChartContext createChartWithStarInMingPalace(String starCode) {
        return createChart(starCode, null, DiZhiEnum.ZI);
    }

    private ChartContext createChartWithStarAtDiZhi(String starCode, DiZhiEnum targetDiZhi) {
        return createChart(starCode, null, targetDiZhi);
    }

    private ChartContext createEmptyChart() {
        return createChart(null, null, null);
    }

    private ChartContext createChart(String mingStarCode, String mingStarCode2, DiZhiEnum mingDiZhi) {
        EnumMap<PalaceTypeEnum, Palace> palaces = new EnumMap<>(PalaceTypeEnum.class);

        for (PalaceTypeEnum type : PalaceTypeEnum.values()) {
            DiZhiEnum diZhi = DiZhiEnum.ofIndex((type.getCode() - 1 + 2) % 12);
            Palace palace = Palace.builder()
                    .type(type)
                    .diZhi(diZhi)
                    .tianGan(TianGanEnum.JIA)
                    .isShenGong(false)
                    .stars(new ArrayList<>())
                    .build();

            if (type == PalaceTypeEnum.MING_GONG && mingDiZhi != null) {
                palace.setDiZhi(mingDiZhi);
            }

            // 如果是目标宫位且有星要放
            if (type == PalaceTypeEnum.MING_GONG && mingStarCode != null) {
                StarPosition sp = StarPosition.builder()
                        .starCode(mingStarCode)
                        .starName(Star.getNameByCode(mingStarCode))
                        .starType(StarTypeEnum.MAJOR)
                        .build();
                palace.addStar(sp);
                if (mingStarCode2 != null) {
                    StarPosition sp2 = StarPosition.builder()
                            .starCode(mingStarCode2)
                            .starName(Star.getNameByCode(mingStarCode2))
                            .starType(StarTypeEnum.MAJOR)
                            .build();
                    palace.addStar(sp2);
                }
            }

            palaces.put(type, palace);
        }

        return ChartContext.builder()
                .solarYear(2000).solarMonth(1).solarDay(1).solarHour(12).solarMinute(0)
                .gender(GenderEnum.MALE)
                .yearTianGan(TianGanEnum.JIA).yearDiZhi(DiZhiEnum.ZI)
                .mingGongDiZhi(mingDiZhi != null ? mingDiZhi : DiZhiEnum.ZI)
                .wuxingJu("水二局")
                .palaces(palaces)
                .natalSihua(new ArrayList<>())
                .daxianCycles(new ArrayList<>())
                .build();
    }

}
