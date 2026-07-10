package cn.iocoder.yudao.module.ziwei.service.cycle;

import cn.iocoder.yudao.module.ziwei.dal.dataobject.*;
import cn.iocoder.yudao.module.ziwei.dal.mysql.*;
import cn.iocoder.yudao.module.ziwei.engine.cycle.DaxianEngine;
import cn.iocoder.yudao.module.ziwei.engine.cycle.LiuninEngine;
import cn.iocoder.yudao.module.ziwei.engine.model.*;
import cn.iocoder.yudao.module.ziwei.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 大限/流年 Service 实现
 *
 * @author JTWORLD
 */
@Service
@Slf4j
public class ZiweiCycleServiceImpl implements ZiweiCycleService {

    @Resource private ZiweiChartMapper chartMapper;
    @Resource private ZiweiPalaceMapper palaceMapper;
    @Resource private ZiweiStarPositionMapper starPositionMapper;
    @Resource private ZiweiSihuaMapper sihuaMapper;
    @Resource private ZiweiDaxianMapper daxianMapper;

    private final LiuninEngine liuninEngine = new LiuninEngine();

    @Override
    public List<DaxianCycle> getDaxianList(Long chartId) {
        List<ZiweiDaxianDO> daxianDOs = daxianMapper.selectListByChartId(chartId);
        return daxianDOs.stream().map(this::toDaxianCycle).collect(Collectors.toList());
    }

    @Override
    public LiuninResult getLiunin(Long chartId, int year) {
        ChartContext ctx = buildChartContext(chartId);
        if (ctx == null) return null;
        return liuninEngine.calculateLiunin(ctx, year);
    }

    @Override
    public List<LiuninResult> getLiuninRange(Long chartId, int startYear, int endYear) {
        ChartContext ctx = buildChartContext(chartId);
        if (ctx == null) return Collections.emptyList();
        return liuninEngine.calculateLiuninRange(ctx, startYear, endYear);
    }

    /**
     * 从数据库重建 ChartContext
     */
    private ChartContext buildChartContext(Long chartId) {
        ZiweiChartDO chartDO = chartMapper.selectById(chartId);
        if (chartDO == null) return null;

        // 查询宫位
        List<ZiweiPalaceDO> palaceDOs = palaceMapper.selectListByChartId(chartId);
        // 查询星曜
        List<ZiweiStarPositionDO> starPositionDOs = starPositionMapper.selectListByChartId(chartId);

        Map<Long, List<ZiweiStarPositionDO>> starsByPalaceId = starPositionDOs.stream()
                .collect(Collectors.groupingBy(ZiweiStarPositionDO::getPalaceId));

        // 构建宫位
        EnumMap<PalaceTypeEnum, Palace> palaces = new EnumMap<>(PalaceTypeEnum.class);
        DiZhiEnum mingGongDiZhi = null;
        for (ZiweiPalaceDO pDO : palaceDOs) {
            PalaceTypeEnum type = PalaceTypeEnum.ofCode(pDO.getPalaceType());
            DiZhiEnum diZhi = DiZhiEnum.ofName(pDO.getDizhi());
            TianGanEnum tianGan = pDO.getTianGan() != null ? TianGanEnum.ofName(pDO.getTianGan()) : null;

            Palace palace = Palace.builder()
                    .type(type).diZhi(diZhi).tianGan(tianGan)
                    .isShenGong(pDO.getIsShenGong() != null && pDO.getIsShenGong())
                    .daXianStartAge(pDO.getDaXianStartAge())
                    .daXianEndAge(pDO.getDaXianEndAge())
                    .stars(new ArrayList<>())
                    .build();

            List<ZiweiStarPositionDO> palaceStars = starsByPalaceId.getOrDefault(pDO.getId(), Collections.emptyList());
            for (ZiweiStarPositionDO spDO : palaceStars) {
                StarPosition sp = StarPosition.builder()
                        .starCode(spDO.getStarCode())
                        .starName(Star.getNameByCode(spDO.getStarCode()))
                        .starType(StarTypeEnum.ofCode(spDO.getStarType()))
                        .sihuaType(spDO.getSihuaType() != null ? SihuaTypeEnum.ofCode(spDO.getSihuaType()) : null)
                        .build();
                palace.addStar(sp);
            }
            palaces.put(type, palace);
            if (type == PalaceTypeEnum.MING_GONG) mingGongDiZhi = diZhi;
        }

        // 解析年柱
        TianGanEnum yearGan = null;
        DiZhiEnum yearZhi = null;
        if (chartDO.getYearPillar() != null && chartDO.getYearPillar().length() >= 2) {
            yearGan = TianGanEnum.ofName(String.valueOf(chartDO.getYearPillar().charAt(0)));
            yearZhi = DiZhiEnum.ofName(String.valueOf(chartDO.getYearPillar().charAt(1)));
        }

        return ChartContext.builder()
                .solarYear(chartDO.getSolarYear())
                .yearTianGan(yearGan).yearDiZhi(yearZhi)
                .mingGongDiZhi(mingGongDiZhi)
                .palaces(palaces)
                .build();
    }

    private DaxianCycle toDaxianCycle(ZiweiDaxianDO dDO) {
        return DaxianCycle.builder()
                .sequenceOrder(dDO.getSequenceOrder())
                .ageStart(dDO.getAgeStart()).ageEnd(dDO.getAgeEnd())
                .calYearStart(dDO.getCalYearStart()).calYearEnd(dDO.getCalYearEnd())
                .palaceType(PalaceTypeEnum.ofCode(dDO.getPalaceType()))
                .forward(dDO.getDirection() != null && dDO.getDirection())
                .build();
    }

}
