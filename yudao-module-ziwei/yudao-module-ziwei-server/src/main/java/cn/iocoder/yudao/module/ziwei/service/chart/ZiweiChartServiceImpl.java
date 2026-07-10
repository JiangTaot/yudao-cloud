package cn.iocoder.yudao.module.ziwei.service.chart;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartCreateReqVO;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartPageReqVO;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartRespVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.*;
import cn.iocoder.yudao.module.ziwei.dal.mysql.*;
import cn.iocoder.yudao.module.ziwei.engine.calendar.BaziCalculator;
import cn.iocoder.yudao.module.ziwei.engine.calendar.LunarCalendarConverter;
import cn.iocoder.yudao.module.ziwei.engine.cycle.DaxianEngine;
import cn.iocoder.yudao.module.ziwei.engine.model.*;
import cn.iocoder.yudao.module.ziwei.engine.palace.MingGongCalculator;
import cn.iocoder.yudao.module.ziwei.engine.palace.PalaceSetupEngine;
import cn.iocoder.yudao.module.ziwei.engine.palace.WuxingJuCalculator;
import cn.iocoder.yudao.module.ziwei.engine.pattern.PatternMatcher;
import cn.iocoder.yudao.module.ziwei.engine.star.*;
import cn.iocoder.yudao.module.ziwei.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 紫微斗数命盘 Service 实现
 *
 * @author JTWORLD
 */
@Service
@Slf4j
public class ZiweiChartServiceImpl implements ZiweiChartService {

    @Resource private ZiweiChartMapper chartMapper;
    @Resource private ZiweiPalaceMapper palaceMapper;
    @Resource private ZiweiStarPositionMapper starPositionMapper;
    @Resource private ZiweiSihuaMapper sihuaMapper;
    @Resource private ZiweiDaxianMapper daxianMapper;

    // ========== 引擎（纯 Java，无状态） ==========
    private final LunarCalendarConverter lunarConverter = new LunarCalendarConverter();
    private final BaziCalculator baziCalculator = new BaziCalculator();
    private final MingGongCalculator mingGongCalculator = new MingGongCalculator();
    private final PalaceSetupEngine palaceSetupEngine = new PalaceSetupEngine();
    private final WuxingJuCalculator wuxingJuCalculator = new WuxingJuCalculator();
    private final ZiweiStarPlacer ziweiPlacer = new ZiweiStarPlacer();
    private final TianfuStarPlacer tianfuPlacer = new TianfuStarPlacer();
    private final AuxiliaryStarPlacer auxiliaryPlacer = new AuxiliaryStarPlacer();
    private final MinorStarPlacer minorPlacer = new MinorStarPlacer();
    private final SihuaCalculator sihuaCalculator = new SihuaCalculator();
    private final DaxianEngine daxianEngine = new DaxianEngine();
    private final PatternMatcher patternMatcher = new PatternMatcher();
    private final BrightnessCalculator brightnessCalculator = new BrightnessCalculator();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ZiweiChartRespVO calculateAndSave(ZiweiChartCreateReqVO reqVO) {
        // ===== 1. 构建 ChartContext（完整排盘） =====
        ChartContext ctx = buildChartContext(reqVO);

        // ===== 2. 持久化 =====
        ZiweiChartDO chartDO = saveChart(ctx, reqVO.getUserId());

        // ===== 3. 返回 VO =====
        return toRespVO(chartDO, ctx);
    }

    /**
     * 核心排盘流程
     */
    private ChartContext buildChartContext(ZiweiChartCreateReqVO reqVO) {
        int year = reqVO.getSolarYear();
        int month = reqVO.getSolarMonth();
        int day = reqVO.getSolarDay();
        int hour = reqVO.getSolarHour();
        int minute = reqVO.getSolarMinute() != null ? reqVO.getSolarMinute() : 0;
        GenderEnum gender = GenderEnum.ofCode(reqVO.getGender());

        // Step 1: 八字排盘
        Bazi bazi = baziCalculator.calculate(year, month, day, hour, minute);
        TianGanEnum yearGan = bazi.getYearPillar().getTianGan();
        DiZhiEnum yearZhi = bazi.getYearPillar().getDiZhi();

        // Step 2: 阴历转换
        LunarCalendarConverter.LunarResult lunar = lunarConverter.solarToLunar(year, month, day, hour, minute);

        // Step 3: 命宫 + 身宫
        DiZhiEnum hourZhi = DiZhiEnum.ofHour(hour);
        DiZhiEnum mingGongDiZhi = mingGongCalculator.calculateMingGong(lunar.lunarMonth(), hourZhi);
        DiZhiEnum shenGongDiZhi = mingGongCalculator.calculateShenGong(lunar.lunarMonth(), hourZhi);

        // Step 4: 十二宫排列 + 宫干
        EnumMap<PalaceTypeEnum, Palace> palaces = palaceSetupEngine.setupPalaces(mingGongDiZhi, shenGongDiZhi, yearGan);
        Palace mingPalace = palaces.get(PalaceTypeEnum.MING_GONG);
        TianGanEnum mingPalaceGan = mingPalace.getTianGan();

        // Step 5: 五行局
        int wuxingJuNum = wuxingJuCalculator.calculateJuNumber(mingPalaceGan, mingGongDiZhi);
        String wuxingJuName = wuxingJuCalculator.getJuName(mingPalaceGan, mingGongDiZhi);

        // Step 6: 安紫微星
        DiZhiEnum ziweiDiZhi = ziweiPlacer.findZiweiPosition(lunar.lunarDay(), wuxingJuNum);

        // Step 7: 安十四主星
        Map<String, DiZhiEnum> ziweiGroup = ziweiPlacer.placeZiweiGroup(ziweiDiZhi);
        DiZhiEnum tianfuDiZhi = tianfuPlacer.findTianfuPosition(ziweiDiZhi);
        Map<String, DiZhiEnum> tianfuGroup = tianfuPlacer.placeTianfuGroup(tianfuDiZhi);

        // Step 8: 安辅星 + 杂曜
        Map<String, DiZhiEnum> auxStars = auxiliaryPlacer.placeAll(lunar.lunarMonth(), hourZhi, yearGan, yearZhi);
        Map<String, DiZhiEnum> minorStars = minorPlacer.placeAll(lunar.lunarMonth(), hourZhi, yearZhi, yearGan);

        // Step 9: 填充星曜到宫位
        placeStarsIntoPalaces(palaces, ziweiGroup, tianfuGroup, auxStars, minorStars);

        // Step 10: 四化
        List<StarPosition> natalSihua = sihuaCalculator.calculateNatalSihua(yearGan);
        applySihuaToPalaces(palaces, natalSihua);

        // Step 11: 大限
        List<DaxianCycle> daxianCycles = daxianEngine.calculate(palaces, mingGongDiZhi, gender, yearGan, wuxingJuNum, year);

        // Step 12: 格局匹配
        ChartContext ctx = ChartContext.builder()
                .solarYear(year).solarMonth(month).solarDay(day).solarHour(hour).solarMinute(minute)
                .gender(gender).birthPlace(reqVO.getBirthPlace())
                .isDst(reqVO.getIsDst() != null && reqVO.getIsDst())
                .lunarYear(lunar.lunarYear()).lunarMonth(lunar.lunarMonth()).lunarDay(lunar.lunarDay())
                .isLeapMonth(lunar.isLeapMonth())
                .bazi(bazi).yearTianGan(yearGan).yearDiZhi(yearZhi)
                .mingGongDiZhi(mingGongDiZhi).shenGongDiZhi(shenGongDiZhi)
                .wuxingJu(wuxingJuName).wuxingJuNumber(wuxingJuNum)
                .palaces(palaces).natalSihua(natalSihua).daxianCycles(daxianCycles)
                .build();

        List<cn.iocoder.yudao.module.ziwei.engine.pattern.PatternResult> patternResults = patternMatcher.matchPatternsDetailed(ctx);
        ctx.setPatternResults(patternResults);
        ctx.setPatternNames(patternMatcher.matchPatterns(ctx));

        return ctx;
    }

    /**
     * 将星曜填入对应宫位
     */
    private void placeStarsIntoPalaces(EnumMap<PalaceTypeEnum, Palace> palaces,
                                        Map<String, DiZhiEnum> ziweiGroup,
                                        Map<String, DiZhiEnum> tianfuGroup,
                                        Map<String, DiZhiEnum> auxStars,
                                        Map<String, DiZhiEnum> minorStars) {
        // 主星
        placeGroup(palaces, ziweiGroup, StarTypeEnum.MAJOR, false);
        placeGroup(palaces, tianfuGroup, StarTypeEnum.MAJOR, false);
        // 辅星
        placeGroup(palaces, auxStars, StarTypeEnum.AUXILIARY, true);
        // 杂曜
        placeGroup(palaces, minorStars, StarTypeEnum.MINOR, true);
    }

    private void placeGroup(EnumMap<PalaceTypeEnum, Palace> palaces, Map<String, DiZhiEnum> starMap,
                            StarTypeEnum type, boolean isAuxOrMinor) {
        for (Map.Entry<String, DiZhiEnum> entry : starMap.entrySet()) {
            String starCode = entry.getKey();
            DiZhiEnum diZhi = entry.getValue();
            String starName = Star.getNameByCode(starCode);
            StarBrightnessEnum brightness = brightnessCalculator.calculate(starCode, diZhi);

            for (Palace palace : palaces.values()) {
                if (palace.getDiZhi() == diZhi) {
                    StarPosition sp = StarPosition.builder()
                            .starCode(starCode)
                            .starName(starName)
                            .starType(type)
                            .brightness(brightness)
                            .build();
                    palace.addStar(sp);
                    break;
                }
            }
        }
    }

    /**
     * 将四化应用到星曜（标注四化属性）
     */
    private void applySihuaToPalaces(EnumMap<PalaceTypeEnum, Palace> palaces, List<StarPosition> sihuaList) {
        for (StarPosition sihua : sihuaList) {
            for (Palace palace : palaces.values()) {
                for (StarPosition star : palace.getStars()) {
                    if (star.getStarCode().equals(sihua.getStarCode())) {
                        star.setSihuaType(sihua.getSihuaType());
                    }
                }
            }
        }
    }

    // ========== 持久化 ==========

    private ZiweiChartDO saveChart(ChartContext ctx, Long userId) {
        // 序列化完整命盘 JSON 备份
        String chartJson = null;
        try {
            chartJson = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(ctx);
        } catch (Exception e) {
            log.warn("序列化 chart_json 失败: {}", e.getMessage());
        }

        ZiweiChartDO chartDO = ZiweiChartDO.builder()
                .userId(userId)
                .solarYear(ctx.getSolarYear()).solarMonth(ctx.getSolarMonth())
                .solarDay(ctx.getSolarDay()).solarHour(ctx.getSolarHour()).solarMinute(ctx.getSolarMinute())
                .gender(ctx.getGender().getCode())
                .birthPlace(ctx.getBirthPlace()).isDst(ctx.isDst())
                .lunarYear(ctx.getLunarYear()).lunarMonth(ctx.getLunarMonth()).lunarDay(ctx.getLunarDay())
                .isLeapMonth(ctx.isLeapMonth())
                .yearPillar(ctx.getBazi().getYearPillar().toString())
                .monthPillar(ctx.getBazi().getMonthPillar().toString())
                .dayPillar(ctx.getBazi().getDayPillar().toString())
                .hourPillar(ctx.getBazi().getHourPillar().toString())
                .mingGongDizhi(ctx.getMingGongDiZhi().getName())
                .shenGongDizhi(ctx.getShenGongDiZhi().getName())
                .wuxingJu(ctx.getWuxingJu())
                .chartJson(chartJson)
                .build();
        chartMapper.insert(chartDO);
        Long chartId = chartDO.getId();

        // 保存宫位
        for (Palace palace : ctx.getPalaces().values()) {
            ZiweiPalaceDO pDO = ZiweiPalaceDO.builder()
                    .chartId(chartId).palaceType(palace.getType().getCode())
                    .dizhi(palace.getDiZhi().getName())
                    .tianGan(palace.getTianGan() != null ? palace.getTianGan().getName() : null)
                    .isShenGong(palace.isShenGong())
                    .daXianStartAge(palace.getDaXianStartAge()).daXianEndAge(palace.getDaXianEndAge())
                    .build();
            palaceMapper.insert(pDO);

            // 保存星曜
            int sortOrder = 0;
            for (StarPosition sp : palace.getStars()) {
                ZiweiStarPositionDO spDO = ZiweiStarPositionDO.builder()
                        .chartId(chartId).palaceId(pDO.getId())
                        .starCode(sp.getStarCode()).starType(sp.getStarType().getCode())
                        .brightness(sp.getBrightness() != null ? sp.getBrightness().getCode() : null)
                        .sihuaType(sp.getSihuaType() != null ? sp.getSihuaType().getCode() : null)
                        .sortOrder(sortOrder++)
                        .build();
                starPositionMapper.insert(spDO);
            }
        }

        // 保存四化
        ZiweiSihuaDO sihuaDO = ZiweiSihuaDO.builder()
                .chartId(chartId).sihuaScope(1)
                .huaLuStarCode(ctx.getNatalSihua().get(0).getStarCode())
                .huaQuanStarCode(ctx.getNatalSihua().get(1).getStarCode())
                .huaKeStarCode(ctx.getNatalSihua().get(2).getStarCode())
                .huaJiStarCode(ctx.getNatalSihua().get(3).getStarCode())
                .build();
        sihuaMapper.insert(sihuaDO);

        // 保存大限
        for (DaxianCycle dc : ctx.getDaxianCycles()) {
            ZiweiDaxianDO dDO = ZiweiDaxianDO.builder()
                    .chartId(chartId).sequenceOrder(dc.getSequenceOrder())
                    .ageStart(dc.getAgeStart()).ageEnd(dc.getAgeEnd())
                    .calYearStart(dc.getCalYearStart()).calYearEnd(dc.getCalYearEnd())
                    .palaceType(dc.getPalaceType().getCode())
                    .direction(dc.isForward())
                    .build();
            daxianMapper.insert(dDO);
        }

        return chartDO;
    }

    // ========== 查询 ==========

    @Override
    public ZiweiChartRespVO getChart(Long id) {
        ZiweiChartDO chartDO = chartMapper.selectById(id);
        if (chartDO == null) return null;
        // 从数据库重建完整命盘数据
        return toRespVO(chartDO, buildChartContextFromDB(id));
    }

    @Override
    public PageResult<ZiweiChartRespVO> getChartPage(ZiweiChartPageReqVO reqVO) {
        PageResult<ZiweiChartDO> page = chartMapper.selectPage(reqVO);
        List<ZiweiChartRespVO> list = page.getList().stream()
                .map(chart -> toRespVO(chart, null))  // 列表查询不含完整星曜数据，性能优先
                .collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal());
    }

    /**
     * 从数据库重建 ChartContext（用于查询已有命盘）
     * <p>
     * 查询所有关联表（宫位、星曜、四化、大限）并重建成内存对象，
     * 避免每次查询都重新排盘计算。
     */
    private ChartContext buildChartContextFromDB(Long chartId) {
        // 1. 查询所有宫位
        List<ZiweiPalaceDO> palaceDOs = palaceMapper.selectListByChartId(chartId);
        // 2. 查询所有星曜
        List<ZiweiStarPositionDO> starPositionDOs = starPositionMapper.selectListByChartId(chartId);
        // 3. 查询四化
        List<ZiweiSihuaDO> sihuaDOs = sihuaMapper.selectListByChartId(chartId);
        // 4. 查询大限
        List<ZiweiDaxianDO> daxianDOs = daxianMapper.selectListByChartId(chartId);

        // --- 按 palaceId 分组星曜 ---
        Map<Long, List<ZiweiStarPositionDO>> starsByPalaceId = starPositionDOs.stream()
                .collect(Collectors.groupingBy(ZiweiStarPositionDO::getPalaceId));

        // --- 构建宫位 ---
        EnumMap<PalaceTypeEnum, Palace> palaces = new EnumMap<>(PalaceTypeEnum.class);
        DiZhiEnum mingGongDiZhi = null;
        DiZhiEnum shenGongDiZhi = null;

        for (ZiweiPalaceDO pDO : palaceDOs) {
            PalaceTypeEnum type = PalaceTypeEnum.ofCode(pDO.getPalaceType());
            DiZhiEnum diZhi = DiZhiEnum.ofName(pDO.getDizhi());
            TianGanEnum tianGan = pDO.getTianGan() != null ? TianGanEnum.ofName(pDO.getTianGan()) : null;
            boolean isShenGong = pDO.getIsShenGong() != null && pDO.getIsShenGong();

            Palace palace = Palace.builder()
                    .type(type)
                    .diZhi(diZhi)
                    .tianGan(tianGan)
                    .isShenGong(isShenGong)
                    .daXianStartAge(pDO.getDaXianStartAge())
                    .daXianEndAge(pDO.getDaXianEndAge())
                    .stars(new ArrayList<>())
                    .build();

            // 填充星曜
            List<ZiweiStarPositionDO> palaceStars = starsByPalaceId.getOrDefault(pDO.getId(), Collections.emptyList());
            for (ZiweiStarPositionDO spDO : palaceStars) {
                StarPosition sp = StarPosition.builder()
                        .starCode(spDO.getStarCode())
                        .starName(Star.getNameByCode(spDO.getStarCode()))
                        .starType(StarTypeEnum.ofCode(spDO.getStarType()))
                        .brightness(spDO.getBrightness() != null ? StarBrightnessEnum.ofCode(spDO.getBrightness()) : null)
                        .sihuaType(spDO.getSihuaType() != null ? SihuaTypeEnum.ofCode(spDO.getSihuaType()) : null)
                        .build();
                palace.addStar(sp);
            }

            palaces.put(type, palace);

            // 记录命宫/身宫地支
            if (type == PalaceTypeEnum.MING_GONG) {
                mingGongDiZhi = diZhi;
            }
            if (isShenGong) {
                shenGongDiZhi = diZhi;
            }
        }

        // --- 构建四化 ---
        List<StarPosition> natalSihua = new ArrayList<>();
        for (ZiweiSihuaDO sDO : sihuaDOs) {
            if (sDO.getSihuaScope() != null && sDO.getSihuaScope() == 1) { // 本命四化
                natalSihua.add(StarPosition.builder()
                        .starCode(sDO.getHuaLuStarCode())
                        .starName(Star.getNameByCode(sDO.getHuaLuStarCode()))
                        .sihuaType(SihuaTypeEnum.HUA_LU)
                        .build());
                natalSihua.add(StarPosition.builder()
                        .starCode(sDO.getHuaQuanStarCode())
                        .starName(Star.getNameByCode(sDO.getHuaQuanStarCode()))
                        .sihuaType(SihuaTypeEnum.HUA_QUAN)
                        .build());
                natalSihua.add(StarPosition.builder()
                        .starCode(sDO.getHuaKeStarCode())
                        .starName(Star.getNameByCode(sDO.getHuaKeStarCode()))
                        .sihuaType(SihuaTypeEnum.HUA_KE)
                        .build());
                natalSihua.add(StarPosition.builder()
                        .starCode(sDO.getHuaJiStarCode())
                        .starName(Star.getNameByCode(sDO.getHuaJiStarCode()))
                        .sihuaType(SihuaTypeEnum.HUA_JI)
                        .build());
                break; // 只取第一条本命四化
            }
        }

        // --- 构建大限 ---
        List<DaxianCycle> daxianCycles = new ArrayList<>();
        for (ZiweiDaxianDO dDO : daxianDOs) {
            PalaceTypeEnum palaceType = PalaceTypeEnum.ofCode(dDO.getPalaceType());
            DaxianCycle dc = DaxianCycle.builder()
                    .sequenceOrder(dDO.getSequenceOrder())
                    .ageStart(dDO.getAgeStart())
                    .ageEnd(dDO.getAgeEnd())
                    .calYearStart(dDO.getCalYearStart())
                    .calYearEnd(dDO.getCalYearEnd())
                    .palaceType(palaceType)
                    .forward(dDO.getDirection() != null && dDO.getDirection())
                    .build();
            daxianCycles.add(dc);
        }

        // --- 构建 ChartContext ---
        // 从 chartDO 获取基础信息，从 DB 获取部分
        ZiweiChartDO chartDO = chartMapper.selectById(chartId);
        TianGanEnum yearGan = null;
        DiZhiEnum yearZhi = null;
        if (chartDO.getYearPillar() != null && chartDO.getYearPillar().length() >= 2) {
            yearGan = TianGanEnum.ofName(String.valueOf(chartDO.getYearPillar().charAt(0)));
            yearZhi = DiZhiEnum.ofName(String.valueOf(chartDO.getYearPillar().charAt(1)));
        }

        ChartContext ctx = ChartContext.builder()
                .solarYear(chartDO.getSolarYear()).solarMonth(chartDO.getSolarMonth())
                .solarDay(chartDO.getSolarDay()).solarHour(chartDO.getSolarHour())
                .solarMinute(chartDO.getSolarMinute())
                .gender(GenderEnum.ofCode(chartDO.getGender()))
                .birthPlace(chartDO.getBirthPlace())
                .isDst(chartDO.getIsDst() != null && chartDO.getIsDst())
                .lunarYear(chartDO.getLunarYear()).lunarMonth(chartDO.getLunarMonth())
                .lunarDay(chartDO.getLunarDay())
                .isLeapMonth(chartDO.getIsLeapMonth() != null && chartDO.getIsLeapMonth())
                .yearTianGan(yearGan).yearDiZhi(yearZhi)
                .mingGongDiZhi(mingGongDiZhi).shenGongDiZhi(shenGongDiZhi)
                .wuxingJu(chartDO.getWuxingJu())
                .palaces(palaces).natalSihua(natalSihua).daxianCycles(daxianCycles)
                .build();

        // --- 格局匹配 ---
        List<String> patterns = patternMatcher.matchPatterns(ctx);
        ctx.setPatternNames(patterns);

        return ctx;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChart(Long id) {
        chartMapper.deleteById(id);
        palaceMapper.deleteByChartId(id);
        starPositionMapper.deleteByChartId(id);
        sihuaMapper.deleteByChartId(id);
        daxianMapper.deleteByChartId(id);
    }

    // ========== VO 转换 ==========

    private ZiweiChartRespVO toRespVO(ZiweiChartDO chartDO, ChartContext ctx) {
        ZiweiChartRespVO.ZiweiChartRespVOBuilder builder = ZiweiChartRespVO.builder()
                .id(chartDO.getId())
                .solarYear(chartDO.getSolarYear()).solarMonth(chartDO.getSolarMonth())
                .solarDay(chartDO.getSolarDay()).solarHour(chartDO.getSolarHour()).solarMinute(chartDO.getSolarMinute())
                .gender(chartDO.getGender()).birthPlace(chartDO.getBirthPlace())
                .yearPillar(chartDO.getYearPillar()).monthPillar(chartDO.getMonthPillar())
                .dayPillar(chartDO.getDayPillar()).hourPillar(chartDO.getHourPillar())
                .mingGong(chartDO.getMingGongDizhi() + "宫")
                .shenGong(chartDO.getShenGongDizhi() + "宫")
                .wuxingJu(chartDO.getWuxingJu())
                .createTime(chartDO.getCreateTime());

        // 从内存对象填充详细信息（星星、四化、大限等）
        if (ctx != null) {
            // 十二宫
            List<ZiweiChartRespVO.PalaceVO> palaceVOs = new ArrayList<>();
            for (PalaceTypeEnum type : PalaceTypeEnum.values()) {
                Palace palace = ctx.getPalace(type);
                if (palace != null) {
                    palaceVOs.add(toPalaceVO(palace));
                }
            }
            builder.palaces(palaceVOs);
            builder.patterns(ctx.getPatternNames());
            // 格局详情
            if (ctx.getPatternResults() != null && !ctx.getPatternResults().isEmpty()) {
                builder.patternDetails(ctx.getPatternResults().stream()
                        .map(this::toPatternDetailVO)
                        .collect(Collectors.toList()));
            }
            builder.natalSihua(toNatalSihuaVO(ctx.getNatalSihua()));
            builder.daxians(ctx.getDaxianCycles().stream().map(this::toDaxianVO).collect(Collectors.toList()));
        }

        return builder.build();
    }

    private ZiweiChartRespVO.PalaceVO toPalaceVO(Palace palace) {
        return ZiweiChartRespVO.PalaceVO.builder()
                .palaceType(palace.getType().getCode())
                .palaceName(palace.getType().getName())
                .dizhi(palace.getDiZhi().getName())
                .tianGan(palace.getTianGan() != null ? palace.getTianGan().getName() : null)
                .isShenGong(palace.isShenGong())
                .daXianLabel(palace.getDaXianLabel())
                .majorStars(palace.getMajorStars().stream().map(this::toStarVO).collect(Collectors.toList()))
                .auxiliaryStars(palace.getAuxiliaryStars().stream().map(this::toStarVO).collect(Collectors.toList()))
                .minorStars(palace.getMinorStars().stream().map(this::toStarVO).collect(Collectors.toList()))
                .build();
    }

    private ZiweiChartRespVO.StarVO toStarVO(StarPosition sp) {
        return ZiweiChartRespVO.StarVO.builder()
                .starCode(sp.getStarCode())
                .starName(sp.getStarName())
                .brightness(sp.getBrightness() != null ? sp.getBrightness().getName() : null)
                .sihuaType(sp.getSihuaType() != null ? sp.getSihuaType().getShortName() : null)
                .build();
    }

    private ZiweiChartRespVO.NatalSihuaVO toNatalSihuaVO(List<StarPosition> sihuaList) {
        if (sihuaList == null || sihuaList.size() < 4) return null;
        return ZiweiChartRespVO.NatalSihuaVO.builder()
                .huaLu(toStarVO(sihuaList.get(0)))
                .huaQuan(toStarVO(sihuaList.get(1)))
                .huaKe(toStarVO(sihuaList.get(2)))
                .huaJi(toStarVO(sihuaList.get(3)))
                .build();
    }

    private ZiweiChartRespVO.DaxianVO toDaxianVO(DaxianCycle dc) {
        return ZiweiChartRespVO.DaxianVO.builder()
                .ageStart(dc.getAgeStart()).ageEnd(dc.getAgeEnd())
                .palaceName(dc.getPalaceType().getName())
                .direction(dc.isForward() ? "顺行" : "逆行")
                .build();
    }

    private ZiweiChartRespVO.PatternDetailVO toPatternDetailVO(cn.iocoder.yudao.module.ziwei.engine.pattern.PatternResult pr) {
        return ZiweiChartRespVO.PatternDetailVO.builder()
                .name(pr.getName())
                .level(pr.getLevel() != null ? pr.getLevel().getName() : null)
                .description(pr.getDescription())
                .source(pr.getSource())
                .required(pr.getRequired())
                .bonus(pr.getBonus())
                .breaking(pr.getBreaking())
                .build();
    }

}
