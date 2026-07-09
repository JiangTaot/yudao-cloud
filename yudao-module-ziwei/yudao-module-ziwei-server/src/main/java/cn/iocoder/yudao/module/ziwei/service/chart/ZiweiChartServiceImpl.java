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
        Map<String, DiZhiEnum> tianfuGroup = tianfuPlacer.placeTianfuGroup(ziweiDiZhi.opposite());

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

        List<String> patterns = patternMatcher.matchPatterns(ctx);
        ctx.setPatternNames(patterns);

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

            for (Palace palace : palaces.values()) {
                if (palace.getDiZhi() == diZhi) {
                    StarPosition sp = StarPosition.builder()
                            .starCode(starCode)
                            .starName(starName)
                            .starType(type)
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
        // 从数据库重建 VO（简化版，不含完整星曜数据）
        return toRespVO(chartDO, null);
    }

    @Override
    public PageResult<ZiweiChartRespVO> getChartPage(ZiweiChartPageReqVO reqVO) {
        PageResult<ZiweiChartDO> page = chartMapper.selectPage(reqVO);
        List<ZiweiChartRespVO> list = page.getList().stream()
                .map(chart -> toRespVO(chart, null))
                .collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal());
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

}
