package cn.iocoder.yudao.module.ziwei.service.cycle;

import cn.iocoder.yudao.module.ziwei.engine.model.DaxianCycle;
import cn.iocoder.yudao.module.ziwei.engine.model.LiuninResult;

import java.util.List;

/**
 * 大限/流年 Service 接口
 *
 * @author JTWORLD
 */
public interface ZiweiCycleService {

    /**
     * 获取命盘的所有大限
     *
     * @param chartId 命盘ID
     * @return 大限列表（12个）
     */
    List<DaxianCycle> getDaxianList(Long chartId);

    /**
     * 获取指定年份的流年信息
     *
     * @param chartId 命盘ID
     * @param year    流年年份
     * @return 流年结果
     */
    LiuninResult getLiunin(Long chartId, int year);

    /**
     * 获取年份范围内的流年信息
     *
     * @param chartId   命盘ID
     * @param startYear 起始年份
     * @param endYear   结束年份
     * @return 流年结果列表
     */
    List<LiuninResult> getLiuninRange(Long chartId, int startYear, int endYear);

}
