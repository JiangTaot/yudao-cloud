package cn.iocoder.yudao.module.ziwei.engine.pattern;

import cn.iocoder.yudao.module.ziwei.engine.model.ChartContext;
import cn.iocoder.yudao.module.ziwei.engine.model.Palace;
import cn.iocoder.yudao.module.ziwei.engine.model.Star;
import cn.iocoder.yudao.module.ziwei.engine.model.StarPosition;
import cn.iocoder.yudao.module.ziwei.engine.star.BrightnessCalculator;
import cn.iocoder.yudao.module.ziwei.enums.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 格局匹配引擎
 * <p>
 * 根据命盘中星曜的配置匹配已知格局，支持亮度判定和三级条件体系（required/bonus/breaking）。
 * 参考《紫微斗数全书》《骨髓赋》等古籍经典。
 *
 * @author JTWORLD
 */
public class PatternMatcher {

    private final BrightnessCalculator brightnessCalc = new BrightnessCalculator();

    /**
     * 匹配格局
     *
     * @param chart 完整命盘
     * @return 匹配到的格局名称列表（兼容旧接口）
     */
    public List<String> matchPatterns(ChartContext chart) {
        List<PatternResult> results = matchPatternsDetailed(chart);
        return results.stream().map(PatternResult::getName).collect(Collectors.toList());
    }

    /**
     * 匹配格局（返回详细信息）
     *
     * @param chart 完整命盘
     * @return 匹配到的格局详情列表
     */
    public List<PatternResult> matchPatternsDetailed(ChartContext chart) {
        List<PatternResult> results = new ArrayList<>();

        // ===== 上格 (EXCELLENT) =====
        checkJunChenQingHui(chart, results);       // 君臣庆会
        checkZiFuTongGong(chart, results);          // 紫府同宫
        checkFuXiangChaoYuan(chart, results);       // 府相朝垣
        checkYangLiangChangLu(chart, results);      // 阳梁昌禄
        checkShuangLuChaoYuan(chart, results);      // 双禄朝垣
        checkSanQiJiaHui(chart, results);           // 三奇嘉会
        checkMingZhuChuHai(chart, results);         // 明珠出海
        checkRiYueJiaMing(chart, results);          // 日月夹命

        // ===== 中格 (GOOD) =====
        checkShaPoLang(chart, results);             // 杀破狼
        checkJiYueTongLiang(chart, results);        // 机月同梁
        checkZiWeiInMing(chart, results);           // 紫微入命
        checkHuoTanGe(chart, results);              // 火贪格
        checkLingTanGe(chart, results);             // 铃贪格
        checkWuTanGe(chart, results);               // 武贪格
        checkLianXiang(chart, results);             // 廉贞天相
        checkWuQiSha(chart, results);               // 武曲七杀
        checkTongLiang(chart, results);             // 天同天梁
        checkRiYueTongGong(chart, results);         // 日月同宫
        checkJuRiTongGong(chart, results);          // 巨日同宫
        checkShiZhongYinYu(chart, results);         // 石中隐玉
        checkHuaLuRuMing(chart, results);           // 化禄入命
        checkHuaLuRuCai(chart, results);            // 化禄入财
        checkHuaQuanRuGuan(chart, results);         // 化权入官
        checkHuaKeRuMingShen(chart, results);       // 化科入命/身

        // ===== 平格 (NEUTRAL) =====
        checkFuBiJiaMing(chart, results);           // 辅弼夹命
        checkChangQuJiaMing(chart, results);        // 昌曲夹命
        checkKuiYueJiaMing(chart, results);         // 魁钺夹命
        checkChangQuTongHui(chart, results);        // 昌曲同会
        checkFuBiTongHui(chart, results);           // 辅弼同会
        checkKuiYueTongHui(chart, results);         // 魁钺同会
        checkKeQuanShuangHui(chart, results);       // 科权双会
        checkMingWuZhengYao(chart, results);        // 命无正曜
        checkZiWeiChaoYuan(chart, results);         // 紫微朝垣
        checkTianFuChaoYuan(chart, results);        // 天府朝垣
        checkRiZhaoLeiMen(chart, results);          // 日照雷门
        checkYueLangTianMen(chart, results);        // 月朗天门
        checkWuQuShouYuan(chart, results);          // 武曲守垣

        // ===== 恶格 (CAUTION) =====
        checkHuaJiRuMingQian(chart, results);       // 化忌入命/迁
        checkYangTuoJiaJi(chart, results);          // 羊陀夹忌
        checkHuoLingJiaMing(chart, results);        // 火铃夹命
        checkKongJieJiaMing(chart, results);        // 空劫夹命
        checkLianShaYang(chart, results);           // 廉杀羊
        checkJuHuoYang(chart, results);             // 巨火羊
        checkMaTouDaiJian(chart, results);          // 马头带箭

        return results;
    }

    // ==================== 上格 (EXCELLENT) ====================

    /**
     * 君臣庆会：紫微入命，左辅右弼同会三方四正
     */
    private void checkJunChenQingHui(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null || !ming.hasStar(Star.CODE_ZIWEI)) return;

        Set<String> allStars = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        boolean hasZuoFu = allStars.contains(Star.CODE_ZUOFU);
        boolean hasYouBi = allStars.contains(Star.CODE_YOUBI);

        if (hasZuoFu && hasYouBi) {
            results.add(PatternResult.builder()
                    .name("君臣庆会格").level(PatternLevel.EXCELLENT)
                    .description("紫微入命，左辅右弼同会三方四正，如帝王得贤臣辅佐，主大富大贵、统御之命。一生贵人不绝，宜走政商高位。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("紫微入命", "左辅右弼三方同会"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        } else if (hasZuoFu || hasYouBi) {
            results.add(PatternResult.builder()
                    .name("君臣庆会格（偏格）").level(PatternLevel.GOOD)
                    .description("紫微入命，得辅弼之一会照。贵人助力稍弱，仍主中层管理之命。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("紫微入命", "左辅或右弼三方会照"))
                    .bonus(Collections.singletonList("辅弼双全会照则入上格"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    /**
     * 紫府同宫：紫微和天府同在命宫或迁移宫
     */
    private void checkZiFuTongGong(ChartContext chart, List<PatternResult> results) {
        for (PalaceTypeEnum type : new PalaceTypeEnum[]{PalaceTypeEnum.MING_GONG, PalaceTypeEnum.QIAN_YI}) {
            Palace p = chart.getPalace(type);
            if (p != null && p.hasStar(Star.CODE_ZIWEI) && p.hasStar(Star.CODE_TIANFU)) {
                results.add(PatternResult.builder()
                        .name("紫府同宫格").level(PatternLevel.EXCELLENT)
                        .description("紫微天府同宫，帝王+财库双星合璧，主大富大贵、权财双收，威仪堂堂。")
                        .source("《紫微斗数全书》")
                        .required(Arrays.asList("紫微天府同宫于命宫或迁移宫"))
                        .involvedPalaces(Collections.singletonList(type.getName()))
                        .build());
                return;
            }
        }
    }

    /**
     * 府相朝垣：天府天相分守命宫三方
     */
    private void checkFuXiangChaoYuan(ChartContext chart, List<PatternResult> results) {
        Set<String> sanFangStars = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        boolean hasFu = sanFangStars.contains(Star.CODE_TIANFU);
        boolean hasXiang = sanFangStars.contains(Star.CODE_TIANXIANG);

        if (hasFu && hasXiang) {
            results.add(PatternResult.builder()
                    .name("府相朝垣格").level(PatternLevel.EXCELLENT)
                    .description("天府天相分守命宫三方，稳重厚实，长于规划与行政，宜走政商、金融、地产。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("命宫三方见天府", "命宫三方见天相"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    /**
     * 阳梁昌禄：太阳+天梁+文昌+禄存在命宫三方
     */
    private void checkYangLiangChangLu(ChartContext chart, List<PatternResult> results) {
        Set<String> sanFangStars = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        boolean hasYang = sanFangStars.contains(Star.CODE_TAIYANG);
        boolean hasLiang = sanFangStars.contains(Star.CODE_TIANLIANG);
        boolean hasChang = sanFangStars.contains(Star.CODE_WENCHANG);
        boolean hasLu = sanFangStars.contains(Star.CODE_LUCUN);

        if (hasYang && hasLiang && hasChang && hasLu) {
            results.add(PatternResult.builder()
                    .name("阳梁昌禄格").level(PatternLevel.EXCELLENT)
                    .description("太阳天梁文昌禄存四曜会于命宫三方，主科举高中、功名显达，宜学术研究、教育、出版、法务。")
                    .source("《骨髓赋》")
                    .required(Arrays.asList("太阳三方见", "天梁三方见", "文昌三方见", "禄存三方见"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    /**
     * 双禄朝垣：化禄+禄存在命宫三方四正
     */
    private void checkShuangLuChaoYuan(ChartContext chart, List<PatternResult> results) {
        List<Palace> scope = chart.getTriadAndOpposite(PalaceTypeEnum.MING_GONG);
        boolean hasHuaLu = false, hasLuCun = false;
        for (Palace p : scope) {
            for (StarPosition sp : p.getStars()) {
                if (sp.getSihuaType() == SihuaTypeEnum.HUA_LU) hasHuaLu = true;
                if (Star.CODE_LUCUN.equals(sp.getStarCode())) hasLuCun = true;
            }
        }
        if (hasHuaLu && hasLuCun) {
            results.add(PatternResult.builder()
                    .name("双禄朝垣格").level(PatternLevel.EXCELLENT)
                    .description("化禄禄存同会命宫三方，财源涌动、衣食丰足。古书云'双禄朝垣，富比陶朱'。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("化禄在三方四正", "禄存在三方四正"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    /**
     * 三奇嘉会：化禄化权化科在命宫三方四正
     */
    private void checkSanQiJiaHui(ChartContext chart, List<PatternResult> results) {
        List<Palace> scope = chart.getTriadAndOpposite(PalaceTypeEnum.MING_GONG);
        boolean hasLu = false, hasQuan = false, hasKe = false;
        for (Palace p : scope) {
            for (StarPosition sp : p.getStars()) {
                if (sp.getSihuaType() == SihuaTypeEnum.HUA_LU) hasLu = true;
                if (sp.getSihuaType() == SihuaTypeEnum.HUA_QUAN) hasQuan = true;
                if (sp.getSihuaType() == SihuaTypeEnum.HUA_KE) hasKe = true;
            }
        }
        if (hasLu && hasQuan && hasKe) {
            results.add(PatternResult.builder()
                    .name("三奇嘉会格").level(PatternLevel.EXCELLENT)
                    .description("化禄化权化科三奇汇聚于命宫三方四正，主才智出众、事业有成、名声远播。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("化禄在三方四正", "化权在三方四正", "化科在三方四正"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    /**
     * 明珠出海：命未空宫，对宫丑日月同辉拱照
     */
    private void checkMingZhuChuHai(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null || ming.getDiZhi() != DiZhiEnum.WEI) return;
        if (!ming.getMajorStars().isEmpty()) return; // 命宫必须有主星空亡

        Palace opposite = chart.getPalace(PalaceTypeEnum.MING_GONG.opposite());
        if (opposite != null && opposite.hasStar(Star.CODE_TAIYANG) && opposite.hasStar(Star.CODE_TAIYIN)) {
            results.add(PatternResult.builder()
                    .name("明珠出海格").level(PatternLevel.EXCELLENT)
                    .description("命未空宫，对宫丑日月同辉拱照，主出生平凡，后天努力出头，宜远赴他乡、学术研究或大公司高位。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("命在未宫", "命宫无主星", "对宫日月同宫"))
                    .involvedPalaces(Arrays.asList("命宫", "迁移宫"))
                    .build());
        }
    }

    /**
     * 日月夹命：太阳太阴分居命宫前后夹照
     */
    private void checkRiYueJiaMing(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null) return;

        DiZhiEnum mingZhi = ming.getDiZhi();
        DiZhiEnum prevZhi = mingZhi.moveCounterClockwise(1);
        DiZhiEnum nextZhi = mingZhi.moveClockwise(1);

        Palace prev = findPalaceByDiZhi(chart.getPalaces(), prevZhi);
        Palace next = findPalaceByDiZhi(chart.getPalaces(), nextZhi);

        boolean hasSunNearby = (prev != null && prev.hasStar(Star.CODE_TAIYANG))
                || (next != null && next.hasStar(Star.CODE_TAIYANG));
        boolean hasMoonNearby = (prev != null && prev.hasStar(Star.CODE_TAIYIN))
                || (next != null && next.hasStar(Star.CODE_TAIYIN));

        if (hasSunNearby && hasMoonNearby) {
            boolean sunBright = brightnessCalc.isBright(Star.CODE_TAIYANG,
                    prev != null && prev.hasStar(Star.CODE_TAIYANG) ? prevZhi : nextZhi);
            boolean moonBright = brightnessCalc.isBright(Star.CODE_TAIYIN,
                    prev != null && prev.hasStar(Star.CODE_TAIYIN) ? prevZhi : nextZhi);

            List<String> breaking = new ArrayList<>();
            if (!sunBright) breaking.add("太阳落陷（夹命无光）");
            if (!moonBright) breaking.add("太阴落陷（夹命无光）");

            PatternLevel level = breaking.isEmpty() ? PatternLevel.EXCELLENT : PatternLevel.NEUTRAL;
            results.add(PatternResult.builder()
                    .name("日月夹命格").level(level)
                    .description("太阳太阴分居命宫两侧夹照，光明磊落，贵人相助，事业蓬勃。须日月不落陷方为真夹。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("命宫前后有太阳", "命宫前后有太阴"))
                    .bonus(Arrays.asList("太阳庙旺", "太阴庙旺"))
                    .breaking(breaking)
                    .involvedPalaces(Arrays.asList("命宫", "前后邻宫"))
                    .build());
        }
    }

    // ==================== 中格 (GOOD) ====================

    /**
     * 杀破狼：七杀+破军+贪狼在命宫三方四正
     */
    private void checkShaPoLang(ChartContext chart, List<PatternResult> results) {
        List<Palace> scope = chart.getTriadAndOpposite(PalaceTypeEnum.MING_GONG);
        Set<String> allStars = new HashSet<>();
        for (Palace p : scope) {
            for (StarPosition sp : p.getStars()) {
                allStars.add(sp.getStarCode());
            }
        }
        boolean hasSha = allStars.contains(Star.CODE_QISHA);
        boolean hasPo = allStars.contains(Star.CODE_POJUN);
        boolean hasTan = allStars.contains(Star.CODE_TANLANG);

        if (hasSha && hasPo && hasTan) {
            results.add(PatternResult.builder()
                    .name("杀破狼格").level(PatternLevel.GOOD)
                    .description("七杀破军贪狼三星会命，开创闯荡之命。一生变动多、不甘平凡，宜创业、军警、业务。中年后稳定守成。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("七杀三方见", "破军三方见", "贪狼三方见"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    /**
     * 机月同梁：天机+太阴+天同+天梁在命宫或迁移宫（至少3颗）
     */
    private void checkJiYueTongLiang(ChartContext chart, List<PatternResult> results) {
        String[] stars = {Star.CODE_TIANJI, Star.CODE_TAIYIN, Star.CODE_TIANTONG, Star.CODE_TIANLIANG};
        Palace ming = chart.getMingPalace();
        Palace qianYi = chart.getPalace(PalaceTypeEnum.QIAN_YI);

        long count = Arrays.stream(stars).filter(s ->
                (ming != null && ming.hasStar(s)) || (qianYi != null && qianYi.hasStar(s))
        ).count();

        if (count >= 3) {
            results.add(PatternResult.builder()
                    .name("机月同梁格").level(PatternLevel.GOOD)
                    .description("天机太阴天同天梁汇聚，主机巧善谋、适合公职文教、企划幕僚。古云'机月同梁作吏人'。")
                    .source("《骨髓赋》")
                    .required(Arrays.asList("命宫/迁移宫有至少3颗机月同梁星"))
                    .involvedPalaces(Arrays.asList("命宫", "迁移宫"))
                    .build());
        }
    }

    /**
     * 紫微入命
     */
    private void checkZiWeiInMing(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming != null && ming.hasStar(Star.CODE_ZIWEI)) {
            boolean bright = brightnessCalc.isBright(Star.CODE_ZIWEI, ming.getDiZhi());
            PatternLevel level = bright ? PatternLevel.GOOD : PatternLevel.NEUTRAL;
            results.add(PatternResult.builder()
                    .name("紫微入命格").level(level)
                    .description("紫微坐命，天生具有领导气质和贵人运。庙旺则贵气十足；落陷则孤高自许。")
                    .source("《紫微斗数全书》")
                    .required(Collections.singletonList("紫微入命"))
                    .bonus(bright ? Collections.singletonList("紫微庙旺") : Collections.emptyList())
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    /**
     * 火贪格：贪狼+火星同宫或三合
     */
    private void checkHuoTanGe(ChartContext chart, List<PatternResult> results) {
        checkTanLangWithShaStar(chart, results, Star.CODE_HUOXING, "火贪格",
                "贪狼遇火星同宫或三合，主突发横财。古云'贪狼遇火，必发横财'，但来得快去得也快，宜见好就收。");
    }

    /**
     * 铃贪格：贪狼+铃星同宫或三合
     */
    private void checkLingTanGe(ChartContext chart, List<PatternResult> results) {
        checkTanLangWithShaStar(chart, results, Star.CODE_LINGXING, "铃贪格",
                "贪狼遇铃星同宫或三合，主横发横破，但爆发力不如火贪格。宜把握时机及时收手。");
    }

    /**
     * 武贪格：武曲贪狼同宫或对照
     */
    private void checkWuTanGe(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null) return;
        Set<String> sanFang = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        if (sanFang.contains(Star.CODE_WUQU) && sanFang.contains(Star.CODE_TANLANG)) {
            results.add(PatternResult.builder()
                    .name("武贪格").level(PatternLevel.GOOD)
                    .description("武曲贪狼汇聚，主武职显达、富中带威。宜军警、金融、实业，需防贪念伤身。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("武曲三方见", "贪狼三方见"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    /**
     * 廉贞天相
     */
    private void checkLianXiang(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming != null && ming.hasStar(Star.CODE_LIANZHEN) && ming.hasStar(Star.CODE_TIANXIANG)) {
            results.add(PatternResult.builder()
                    .name("廉贞天相格").level(PatternLevel.GOOD)
                    .description("廉贞天相同宫，廉贞之才情配天相之稳重，主聪慧而守规矩，宜法律、审计、管理。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("廉贞同宫", "天相同宫"))
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    /**
     * 武曲七杀
     */
    private void checkWuQiSha(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming != null && ming.hasStar(Star.CODE_WUQU) && ming.hasStar(Star.CODE_QISHA)) {
            results.add(PatternResult.builder()
                    .name("武曲七杀格").level(PatternLevel.GOOD)
                    .description("武曲七杀同宫，刚毅果断、执行力强。宜军警、外科、机械工程，需防过于刚猛伤人伤己。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("武曲同宫", "七杀同宫"))
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    /**
     * 天同天梁
     */
    private void checkTongLiang(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming != null && ming.hasStar(Star.CODE_TIANTONG) && ming.hasStar(Star.CODE_TIANLIANG)) {
            results.add(PatternResult.builder()
                    .name("天同天梁格").level(PatternLevel.GOOD)
                    .description("天同天梁同宫，福寿双全、心地仁慈。宜医疗、慈善、宗教、教育，主有福报。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("天同同宫", "天梁同宫"))
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    /**
     * 日月同宫：太阳太阴同在丑或未
     */
    private void checkRiYueTongGong(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null) return;
        if (ming.hasStar(Star.CODE_TAIYANG) && ming.hasStar(Star.CODE_TAIYIN)) {
            DiZhiEnum zhi = ming.getDiZhi();
            boolean isWei = zhi == DiZhiEnum.WEI;
            String desc = isWei
                    ? "日月在未同宫，阴阳双美尤佳，主异性缘佳、事业顺遂、名声远播。"
                    : "日月在丑同宫，力量较未宫稍平，但仍主文武兼备。";
            results.add(PatternResult.builder()
                    .name("日月同宫格").level(PatternLevel.GOOD)
                    .description(desc)
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("太阳同宫", "太阴同宫"))
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    /**
     * 巨日同宫：巨门太阳同在寅或申
     */
    private void checkJuRiTongGong(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null) return;
        if (ming.hasStar(Star.CODE_JUMEN) && ming.hasStar(Star.CODE_TAIYANG)) {
            boolean inYin = ming.getDiZhi() == DiZhiEnum.YIN;
            String desc = inYin
                    ? "巨门太阳在寅，太阳庙旺化解巨门之暗，主口才出众、宜传媒外交法律。"
                    : "巨门太阳在申，太阳渐弱，口舌是非较多，需谨慎言辞。";
            results.add(PatternResult.builder()
                    .name("巨日同宫格").level(PatternLevel.GOOD)
                    .description(desc)
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("巨门同宫", "太阳同宫"))
                    .bonus(inYin ? Collections.singletonList("寅宫太阳庙旺化解巨门之暗") : Collections.emptyList())
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    /**
     * 石中隐玉：巨门子午入命
     */
    private void checkShiZhongYinYu(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null || !ming.hasStar(Star.CODE_JUMEN)) return;
        if (ming.getDiZhi() != DiZhiEnum.ZI && ming.getDiZhi() != DiZhiEnum.WU) return;

        Set<String> sanFang = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        boolean hasChang = sanFang.contains(Star.CODE_WENCHANG);
        PatternLevel level = hasChang ? PatternLevel.GOOD : PatternLevel.NEUTRAL;

        results.add(PatternResult.builder()
                .name("石中隐玉格").level(level)
                .description("巨门坐命子午，外表平凡内蕴才学。早年默默无闻、中年显贵气，宜专业、研究、口才、传媒。需文昌会照方显。")
                .source("《紫微斗数全书》")
                .required(Collections.singletonList("巨门在子/午入命"))
                .bonus(hasChang ? Collections.singletonList("文昌会照（石中隐玉得明）") : Collections.emptyList())
                .involvedPalaces(Collections.singletonList("命宫"))
                .build());
    }

    /**
     * 化禄入命
     */
    private void checkHuaLuRuMing(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null) return;
        for (StarPosition sp : ming.getMajorStars()) {
            if (sp.getSihuaType() == SihuaTypeEnum.HUA_LU) {
                results.add(PatternResult.builder()
                        .name("化禄入命格").level(PatternLevel.GOOD)
                        .description(sp.getStarName() + "化禄坐命，主生财顺利、人缘佳、机缘多。")
                        .source("《紫微斗数全书》")
                        .required(Collections.singletonList("命宫主星化禄"))
                        .involvedPalaces(Collections.singletonList("命宫"))
                        .build());
                return;
            }
        }
    }

    /**
     * 化禄入财帛
     */
    private void checkHuaLuRuCai(ChartContext chart, List<PatternResult> results) {
        Palace cai = chart.getPalace(PalaceTypeEnum.CAI_BO);
        if (cai == null) return;
        for (StarPosition sp : cai.getMajorStars()) {
            if (sp.getSihuaType() == SihuaTypeEnum.HUA_LU) {
                results.add(PatternResult.builder()
                        .name("化禄入财帛格").level(PatternLevel.GOOD)
                        .description(sp.getStarName() + "化禄入财帛宫，主财运亨通、收入丰裕。宜经商、投资、金融。")
                        .source("《紫微斗数全书》")
                        .required(Collections.singletonList("财帛宫主星化禄"))
                        .involvedPalaces(Collections.singletonList("财帛宫"))
                        .build());
                return;
            }
        }
    }

    /**
     * 化权入官禄
     */
    private void checkHuaQuanRuGuan(ChartContext chart, List<PatternResult> results) {
        Palace guan = chart.getPalace(PalaceTypeEnum.GUAN_LU);
        if (guan == null) return;
        for (StarPosition sp : guan.getMajorStars()) {
            if (sp.getSihuaType() == SihuaTypeEnum.HUA_QUAN) {
                results.add(PatternResult.builder()
                        .name("化权入官禄格").level(PatternLevel.GOOD)
                        .description(sp.getStarName() + "化权入官禄宫，主事业有权柄、掌实权。宜管理层、创业、军政。")
                        .source("《紫微斗数全书》")
                        .required(Collections.singletonList("官禄宫主星化权"))
                        .involvedPalaces(Collections.singletonList("官禄宫"))
                        .build());
                return;
            }
        }
    }

    /**
     * 化科入命/身
     */
    private void checkHuaKeRuMingShen(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        Palace shen = null;
        for (Palace p : chart.getPalaces().values()) {
            if (p.isShenGong()) { shen = p; break; }
        }

        for (Palace target : new Palace[]{ming, shen}) {
            if (target == null) continue;
            for (StarPosition sp : target.getMajorStars()) {
                if (sp.getSihuaType() == SihuaTypeEnum.HUA_KE) {
                    results.add(PatternResult.builder()
                            .name("化科入" + (target == ming ? "命" : "身") + "格").level(PatternLevel.GOOD)
                            .description(sp.getStarName() + "化科入" + (target == ming ? "命" : "身") + "宫，主才艺出众、名声显达。宜学术、艺术、文教。")
                            .source("《紫微斗数全书》")
                            .required(Collections.singletonList("命/身宫主星化科"))
                            .involvedPalaces(Collections.singletonList(target == ming ? "命宫" : "身宫"))
                            .build());
                    return;
                }
            }
        }
    }

    // ==================== 平格 (NEUTRAL) ====================

    private void checkFuBiJiaMing(ChartContext chart, List<PatternResult> results) {
        checkJiaMing(chart, results, Star.CODE_ZUOFU, Star.CODE_YOUBI, "辅弼夹命格",
                "左辅右弼夹命宫，主贵人夹辅、事业多助，宜管理、行政、辅佐之职。");
    }

    private void checkChangQuJiaMing(ChartContext chart, List<PatternResult> results) {
        checkJiaMing(chart, results, Star.CODE_WENCHANG, Star.CODE_WENQU, "昌曲夹命格",
                "文昌文曲夹命宫，主聪明俊秀、文采斐然，宜文教、学术、艺术、写作。古云'昌曲夹命主科甲'。");
    }

    private void checkKuiYueJiaMing(ChartContext chart, List<PatternResult> results) {
        checkJiaMing(chart, results, Star.CODE_TIANKUI, Star.CODE_TIANYUE, "魁钺夹命格",
                "天魁天钺夹命宫，主贵人提拔、考试运佳，宜公职、大型机构发展。");
    }

    private void checkChangQuTongHui(ChartContext chart, List<PatternResult> results) {
        checkTwoStarsInTriad(chart, results, Star.CODE_WENCHANG, Star.CODE_WENQU, "昌曲同会格",
                "文昌文曲同会命宫三方四正，主文才出众、考试运佳。");
    }

    private void checkFuBiTongHui(ChartContext chart, List<PatternResult> results) {
        checkTwoStarsInTriad(chart, results, Star.CODE_ZUOFU, Star.CODE_YOUBI, "辅弼同会格",
                "左辅右弼同会命宫三方四正，主贵人运强、左右逢源。");
    }

    private void checkKuiYueTongHui(ChartContext chart, List<PatternResult> results) {
        checkTwoStarsInTriad(chart, results, Star.CODE_TIANKUI, Star.CODE_TIANYUE, "魁钺同会格",
                "天魁天钺同会命宫三方四正，主贵人提拔、考试运佳。");
    }

    private void checkKeQuanShuangHui(ChartContext chart, List<PatternResult> results) {
        List<Palace> scope = chart.getTriadAndOpposite(PalaceTypeEnum.MING_GONG);
        boolean hasQuan = false, hasKe = false;
        for (Palace p : scope) {
            for (StarPosition sp : p.getStars()) {
                if (sp.getSihuaType() == SihuaTypeEnum.HUA_QUAN) hasQuan = true;
                if (sp.getSihuaType() == SihuaTypeEnum.HUA_KE) hasKe = true;
            }
        }
        if (hasQuan && hasKe) {
            results.add(PatternResult.builder()
                    .name("科权双会格").level(PatternLevel.NEUTRAL)
                    .description("化权化科同会命宫三方，有权有才、名声事业双收。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("化权在三方四正", "化科在三方四正"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    private void checkMingWuZhengYao(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming != null && ming.getMajorStars().isEmpty()) {
            results.add(PatternResult.builder()
                    .name("命无正曜格").level(PatternLevel.NEUTRAL)
                    .description("命宫无主星，主一生需借对宫之力，随环境而转。宜借势发展，不宜强行出头。")
                    .source("《紫微斗数全书》")
                    .required(Collections.singletonList("命宫无主星"))
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    private void checkZiWeiChaoYuan(ChartContext chart, List<PatternResult> results) {
        if (hasStarInPalaces(chart, Star.CODE_ZIWEI, PalaceTypeEnum.MING_GONG, PalaceTypeEnum.QIAN_YI)) {
            results.add(PatternResult.builder()
                    .name("紫微朝垣格").level(PatternLevel.NEUTRAL)
                    .description("紫微在命宫或迁移宫，主有领导气质和贵人运。")
                    .source("《紫微斗数全书》")
                    .required(Collections.singletonList("紫微在命宫或迁移宫"))
                    .involvedPalaces(Arrays.asList("命宫", "迁移宫"))
                    .build());
        }
    }

    private void checkTianFuChaoYuan(ChartContext chart, List<PatternResult> results) {
        if (hasStarInPalaces(chart, Star.CODE_TIANFU, PalaceTypeEnum.MING_GONG, PalaceTypeEnum.QIAN_YI)) {
            results.add(PatternResult.builder()
                    .name("天府朝垣格").level(PatternLevel.NEUTRAL)
                    .description("天府在命宫或迁移宫，主稳重厚实、理财有方。")
                    .source("《紫微斗数全书》")
                    .required(Collections.singletonList("天府在命宫或迁移宫"))
                    .involvedPalaces(Arrays.asList("命宫", "迁移宫"))
                    .build());
        }
    }

    private void checkRiZhaoLeiMen(ChartContext chart, List<PatternResult> results) {
        if (hasStarAtDiZhi(chart, Star.CODE_TAIYANG, DiZhiEnum.MAO.getName())) {
            results.add(PatternResult.builder()
                    .name("日照雷门格").level(PatternLevel.NEUTRAL)
                    .description("太阳在卯宫，如旭日东升，主光明磊落、名声远播。")
                    .source("《紫微斗数全书》")
                    .required(Collections.singletonList("太阳在卯宫"))
                    .involvedPalaces(Collections.singletonList("卯宫"))
                    .build());
        }
    }

    private void checkYueLangTianMen(ChartContext chart, List<PatternResult> results) {
        if (hasStarAtDiZhi(chart, Star.CODE_TAIYIN, DiZhiEnum.HAI.getName())) {
            results.add(PatternResult.builder()
                    .name("月朗天门格").level(PatternLevel.NEUTRAL)
                    .description("太阴在亥宫，如明月当空，主文采风流、异性缘佳。")
                    .source("《紫微斗数全书》")
                    .required(Collections.singletonList("太阴在亥宫"))
                    .involvedPalaces(Collections.singletonList("亥宫"))
                    .build());
        }
    }

    private void checkWuQuShouYuan(ChartContext chart, List<PatternResult> results) {
        if (hasStarInPalaces(chart, Star.CODE_WUQU, PalaceTypeEnum.MING_GONG)) {
            results.add(PatternResult.builder()
                    .name("武曲守垣格").level(PatternLevel.NEUTRAL)
                    .description("武曲坐命，主刚毅果断、理财有方，宜金融、实业、军警。")
                    .source("《紫微斗数全书》")
                    .required(Collections.singletonList("武曲入命"))
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    // ==================== 恶格 (CAUTION) ====================

    private void checkHuaJiRuMingQian(ChartContext chart, List<PatternResult> results) {
        for (PalaceTypeEnum type : new PalaceTypeEnum[]{PalaceTypeEnum.MING_GONG, PalaceTypeEnum.QIAN_YI}) {
            Palace p = chart.getPalace(type);
            if (p == null) continue;
            for (StarPosition sp : p.getMajorStars()) {
                if (sp.getSihuaType() == SihuaTypeEnum.HUA_JI) {
                    results.add(PatternResult.builder()
                            .name("化忌入" + (type == PalaceTypeEnum.MING_GONG ? "命" : "迁") + "格")
                            .level(PatternLevel.CAUTION)
                            .description(sp.getStarName() + "化忌入" + (type == PalaceTypeEnum.MING_GONG ? "命宫" : "迁移宫")
                                    + "，主一生多波折、事倍功半。需以平常心面对，修身养性化解。")
                            .source("《紫微斗数全书》")
                            .required(Collections.singletonList("命/迁宫主星化忌"))
                            .involvedPalaces(Collections.singletonList(type.getName()))
                            .build());
                    return;
                }
            }
        }
    }

    private void checkYangTuoJiaJi(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null) return;
        boolean hasJi = false;
        for (StarPosition sp : ming.getStars()) {
            if (sp.getSihuaType() == SihuaTypeEnum.HUA_JI) { hasJi = true; break; }
        }
        if (!hasJi) return;

        DiZhiEnum mingZhi = ming.getDiZhi();
        Palace prev = findPalaceByDiZhi(chart.getPalaces(), mingZhi.moveCounterClockwise(1));
        Palace next = findPalaceByDiZhi(chart.getPalaces(), mingZhi.moveClockwise(1));

        boolean hasYangNearby = (prev != null && prev.hasStar(Star.CODE_QINGYANG))
                || (next != null && next.hasStar(Star.CODE_QINGYANG));
        boolean hasTuoNearby = (prev != null && prev.hasStar(Star.CODE_TUOLUO))
                || (next != null && next.hasStar(Star.CODE_TUOLUO));

        if (hasYangNearby && hasTuoNearby) {
            results.add(PatternResult.builder()
                    .name("羊陀夹忌格").level(PatternLevel.CAUTION)
                    .description("擎羊陀罗夹命宫化忌，主多灾多难、刑伤是非。需行善积德、谨慎行事化解。")
                    .source("《骨髓赋》")
                    .required(Arrays.asList("命宫化忌", "命宫前后有擎羊/陀罗"))
                    .involvedPalaces(Arrays.asList("命宫", "前后邻宫"))
                    .build());
        }
    }

    private void checkHuoLingJiaMing(ChartContext chart, List<PatternResult> results) {
        checkJiaMing(chart, results, Star.CODE_HUOXING, Star.CODE_LINGXING, "火铃夹命格",
                "火星铃星夹命宫，主突发性灾祸、性格急躁。需修身养性、沉着应对。");
        // override level to caution
        if (!results.isEmpty() && results.get(results.size() - 1).getName().equals("火铃夹命格")) {
            results.get(results.size() - 1).setLevel(PatternLevel.CAUTION);
        }
    }

    private void checkKongJieJiaMing(ChartContext chart, List<PatternResult> results) {
        checkJiaMing(chart, results, Star.CODE_DIKONG, Star.CODE_DIJIE, "空劫夹命格",
                "地空地劫夹命宫，主人生多波折、理想落空。需脚踏实地、量力而行化解。");
        if (!results.isEmpty() && results.get(results.size() - 1).getName().equals("空劫夹命格")) {
            results.get(results.size() - 1).setLevel(PatternLevel.CAUTION);
        }
    }

    private void checkLianShaYang(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null || !ming.hasStar(Star.CODE_LIANZHEN)) return;
        Set<String> sanFang = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        if (sanFang.contains(Star.CODE_QISHA) && sanFang.contains(Star.CODE_QINGYANG)) {
            results.add(PatternResult.builder()
                    .name("廉杀羊格").level(PatternLevel.CAUTION)
                    .description("廉贞七杀擎羊会于三方，主刑伤、官非、意外。古云'廉杀羊，刑囚夹印'。需合法合规、谨慎行事。")
                    .source("《骨髓赋》")
                    .required(Arrays.asList("廉贞在命", "七杀三方", "擎羊三方"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    private void checkJuHuoYang(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null || !ming.hasStar(Star.CODE_JUMEN)) return;
        Set<String> sanFang = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        if (sanFang.contains(Star.CODE_HUOXING) && sanFang.contains(Star.CODE_QINGYANG)) {
            results.add(PatternResult.builder()
                    .name("巨火羊格").level(PatternLevel.CAUTION)
                    .description("巨门火星擎羊汇聚，主口舌是非升级为官司灾难。古云'巨火羊，终身缢死'，需警惕口舌之争。")
                    .source("《骨髓赋》")
                    .required(Arrays.asList("巨门在命", "火星三方", "擎羊三方"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    private void checkMaTouDaiJian(ChartContext chart, List<PatternResult> results) {
        Palace ming = chart.getMingPalace();
        if (ming == null || ming.getDiZhi() != DiZhiEnum.WU) return;
        if (!ming.hasStar(Star.CODE_TIANMA)) return;
        if (ming.hasStar(Star.CODE_QINGYANG)) {
            results.add(PatternResult.builder()
                    .name("马头带箭格").level(PatternLevel.CAUTION)
                    .description("天马擎羊同宫在午，主奔波劳苦、突发性挫折。但若得吉星化解，反可成'马头带剑'的武将之命。")
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("天马在午", "擎羊同宫"))
                    .involvedPalaces(Collections.singletonList("命宫"))
                    .build());
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取三方四正所有星曜的 starCode 集合
     */
    private Set<String> sanFangAllStarCodes(ChartContext chart, PalaceTypeEnum center) {
        Set<String> codes = new HashSet<>();
        for (Palace p : chart.getTriadAndOpposite(center)) {
            for (StarPosition sp : p.getStars()) {
                codes.add(sp.getStarCode());
            }
        }
        return codes;
    }

    /**
     * 获取三方四正所有星曜的 starName 集合
     */
    private Set<String> sanFangAllStarNames(ChartContext chart, PalaceTypeEnum center) {
        Set<String> names = new HashSet<>();
        for (Palace p : chart.getTriadAndOpposite(center)) {
            for (StarPosition sp : p.getStars()) {
                names.add(sp.getStarName());
                if (sp.getSihuaType() != null) {
                    names.add(sp.getSihuaType().getShortName());
                }
            }
        }
        return names;
    }

    /**
     * 全部十二宫所有星曜 starCode
     */
    private Set<String> allStarCodes(ChartContext chart) {
        Set<String> codes = new HashSet<>();
        for (Palace p : chart.getPalaces().values()) {
            for (StarPosition sp : p.getStars()) {
                codes.add(sp.getStarCode());
            }
        }
        return codes;
    }

    /**
     * 计算贪狼+煞星格局（火贪/铃贪通用）
     */
    private void checkTanLangWithShaStar(ChartContext chart, List<PatternResult> results,
                                          String shaCode, String patternName, String description) {
        Set<String> sanFang = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        if (sanFang.contains(Star.CODE_TANLANG) && sanFang.contains(shaCode)) {
            boolean tanBright = false;
            for (Palace p : chart.getTriadAndOpposite(PalaceTypeEnum.MING_GONG)) {
                if (p.hasStar(Star.CODE_TANLANG)) {
                    tanBright = brightnessCalc.isBright(Star.CODE_TANLANG, p.getDiZhi());
                    break;
                }
            }
            List<String> bonus = new ArrayList<>();
            List<String> breaking = new ArrayList<>();
            if (tanBright) bonus.add("贪狼庙旺（横财有力）");
            else breaking.add("贪狼落陷（发财打折）");

            results.add(PatternResult.builder()
                    .name(patternName).level(PatternLevel.GOOD)
                    .description(description)
                    .source("《骨髓赋》")
                    .required(Arrays.asList("贪狼三方见", shaCode + "三方见"))
                    .bonus(bonus).breaking(breaking)
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    /**
     * 夹命通用检查（两星分居命宫前后）
     */
    private void checkJiaMing(ChartContext chart, List<PatternResult> results,
                               String starCode1, String starCode2, String name, String desc) {
        Palace ming = chart.getMingPalace();
        if (ming == null) return;

        DiZhiEnum mingZhi = ming.getDiZhi();
        Palace prev = findPalaceByDiZhi(chart.getPalaces(), mingZhi.moveCounterClockwise(1));
        Palace next = findPalaceByDiZhi(chart.getPalaces(), mingZhi.moveClockwise(1));

        boolean has1 = (prev != null && prev.hasStar(starCode1)) || (next != null && next.hasStar(starCode1));
        boolean has2 = (prev != null && prev.hasStar(starCode2)) || (next != null && next.hasStar(starCode2));

        if (has1 && has2) {
            results.add(PatternResult.builder()
                    .name(name).level(PatternLevel.NEUTRAL)
                    .description(desc)
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList("命宫前有" + starCode1 + "/" + starCode2,
                            "命宫后有" + starCode1 + "/" + starCode2))
                    .involvedPalaces(Arrays.asList("命宫", "前后邻宫"))
                    .build());
        }
    }

    /**
     * 两星同会在三方四正通用检查
     */
    private void checkTwoStarsInTriad(ChartContext chart, List<PatternResult> results,
                                       String starCode1, String starCode2, String name, String desc) {
        Set<String> sanFang = sanFangAllStarCodes(chart, PalaceTypeEnum.MING_GONG);
        if (sanFang.contains(starCode1) && sanFang.contains(starCode2)) {
            results.add(PatternResult.builder()
                    .name(name).level(PatternLevel.NEUTRAL)
                    .description(desc)
                    .source("《紫微斗数全书》")
                    .required(Arrays.asList(starCode1 + "三方见", starCode2 + "三方见"))
                    .involvedPalaces(Arrays.asList("命宫", "三方四正"))
                    .build());
        }
    }

    // ==================== 基础工具方法 ====================

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

    private Palace findPalaceByDiZhi(EnumMap<PalaceTypeEnum, Palace> palaces, DiZhiEnum diZhi) {
        for (Palace p : palaces.values()) {
            if (p.getDiZhi() == diZhi) return p;
        }
        return null;
    }

}
