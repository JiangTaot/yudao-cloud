package cn.iocoder.yudao.module.ziwei.engine.pattern;

import cn.iocoder.yudao.module.ziwei.engine.model.ChartContext;
import cn.iocoder.yudao.module.ziwei.engine.model.Palace;
import cn.iocoder.yudao.module.ziwei.enums.PalaceTypeEnum;

import java.util.*;
import java.util.function.Predicate;

/**
 * 格局匹配引擎
 * <p>
 * 根据命盘中星曜的配置匹配已知格局（如紫府同宫格、机月同梁格等）。
 * Phase 1 使用硬编码的简化规则，Phase 2 将改为数据库驱动。
 *
 * @author JTWORLD
 */
public class PatternMatcher {

    /**
     * 匹配格局
     *
     * @param chart 完整命盘
     * @return 匹配到的格局名称列表
     */
    public List<String> matchPatterns(ChartContext chart) {
        List<String> results = new ArrayList<>();

        // 紫府同宫格：紫微 + 天府同在命宫
        if (isZiFuTongGong(chart)) results.add("紫府同宫格");

        // 机月同梁格：天机 + 太阴 + 天同 + 天梁在命宫或迁移宫
        if (isJiYueTongLiang(chart)) results.add("机月同梁格");

        // 三奇嘉会格：化禄 + 化权 + 化科同在三方四正
        if (isSanQiJiaHui(chart)) results.add("三奇嘉会格");

        // 命无正曜格：命宫无主星
        if (isMingWuZhengYao(chart)) results.add("命无正曜格");

        // 紫微朝垣格：紫微在命宫或迁移宫
        if (isZiWeiChaoYuan(chart)) results.add("紫微朝垣格");

        // 天府朝垣格：天府在命宫或迁移宫
        if (isTianFuChaoYuan(chart)) results.add("天府朝垣格");

        // 日照雷门格：太阳在卯宫
        if (isRiZhaoLeiMen(chart)) results.add("日照雷门格");

        // 月朗天门格：太阴在亥宫
        if (isYueLangTianMen(chart)) results.add("月朗天门格");

        // 武曲守垣格：武曲在命宫
        if (isWuQuShouYuan(chart)) results.add("武曲守垣格");

        // 杀破狼格：七杀 + 破军 + 贪狼在三方四正
        if (isShaPoLang(chart)) results.add("杀破狼格");

        return results;
    }

    /**
     * 紫府同宫格：紫微和天府同在命宫
     */
    private boolean isZiFuTongGong(ChartContext chart) {
        Palace mingGong = chart.getMingPalace();
        return mingGong != null
                && mingGong.hasStar("ziwei")
                && mingGong.hasStar("tianfu");
    }

    /**
     * 机月同梁格：天机、太阴、天同、天梁中至少3颗在命宫或迁移宫
     */
    private boolean isJiYueTongLiang(ChartContext chart) {
        String[] stars = {"tianji", "taiyin", "tiantong", "tianliang"};
        Palace mingGong = chart.getMingPalace();
        Palace qianYi = chart.getPalace(PalaceTypeEnum.QIAN_YI);

        int count = 0;
        for (String star : stars) {
            if ((mingGong != null && mingGong.hasStar(star))
                    || (qianYi != null && qianYi.hasStar(star))) {
                count++;
            }
        }
        return count >= 3;
    }

    /**
     * 三奇嘉会格：化禄、化权、化科在命宫的三方四正内
     */
    private boolean isSanQiJiaHui(ChartContext chart) {
        List<Palace> scope = chart.getTriadAndOpposite(PalaceTypeEnum.MING_GONG);
        boolean hasLu = false, hasQuan = false, hasKe = false;

        for (Palace p : scope) {
            for (var star : p.getStars()) {
                if (star.getSihuaType() != null) {
                    switch (star.getSihuaType()) {
                        case HUA_LU -> hasLu = true;
                        case HUA_QUAN -> hasQuan = true;
                        case HUA_KE -> hasKe = true;
                    }
                }
            }
        }
        return hasLu && hasQuan && hasKe;
    }

    /**
     * 命无正曜格：命宫没有14主星中的任何一颗
     */
    private boolean isMingWuZhengYao(ChartContext chart) {
        Palace mingGong = chart.getMingPalace();
        return mingGong != null && mingGong.getMajorStars().isEmpty();
    }

    /**
     * 紫微朝垣格：紫微在命宫或迁移宫
     */
    private boolean isZiWeiChaoYuan(ChartContext chart) {
        return hasStarInPalaces(chart, "ziwei",
                PalaceTypeEnum.MING_GONG, PalaceTypeEnum.QIAN_YI);
    }

    /**
     * 天府朝垣格：天府在命宫或迁移宫
     */
    private boolean isTianFuChaoYuan(ChartContext chart) {
        return hasStarInPalaces(chart, "tianfu",
                PalaceTypeEnum.MING_GONG, PalaceTypeEnum.QIAN_YI);
    }

    /**
     * 日照雷门格：太阳在卯宫
     */
    private boolean isRiZhaoLeiMen(ChartContext chart) {
        return hasStarAtDiZhi(chart, "taiyang", "卯");
    }

    /**
     * 月朗天门格：太阴在亥宫
     */
    private boolean isYueLangTianMen(ChartContext chart) {
        return hasStarAtDiZhi(chart, "taiyin", "亥");
    }

    /**
     * 武曲守垣格：武曲在命宫
     */
    private boolean isWuQuShouYuan(ChartContext chart) {
        return hasStarInPalaces(chart, "wuqu", PalaceTypeEnum.MING_GONG);
    }

    /**
     * 杀破狼格：七杀、破军、贪狼在命宫的三方四正
     */
    private boolean isShaPoLang(ChartContext chart) {
        List<Palace> scope = chart.getTriadAndOpposite(PalaceTypeEnum.MING_GONG);
        boolean hasQiSha = false, hasPoJun = false, hasTanLang = false;
        for (Palace p : scope) {
            for (var star : p.getStars()) {
                if ("qisha".equals(star.getStarCode())) hasQiSha = true;
                if ("pojun".equals(star.getStarCode())) hasPoJun = true;
                if ("tanlang".equals(star.getStarCode())) hasTanLang = true;
            }
        }
        return hasQiSha && hasPoJun && hasTanLang;
    }

    // ========== 工具方法 ==========

    private boolean hasStarInPalaces(ChartContext chart, String starCode, PalaceTypeEnum... types) {
        for (PalaceTypeEnum type : types) {
            Palace p = chart.getPalace(type);
            if (p != null && p.hasStar(starCode)) return true;
        }
        return false;
    }

    private boolean hasStarAtDiZhi(ChartContext chart, String starCode, String diZhiName) {
        for (Palace p : chart.getPalaces().values()) {
            if (p.getDiZhi().getName().equals(diZhiName) && p.hasStar(starCode)) {
                return true;
            }
        }
        return false;
    }

}
