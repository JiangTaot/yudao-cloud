package cn.iocoder.yudao.module.ziwei.service.chart;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartCreateReqVO;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartPageReqVO;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartRespVO;

/**
 * 紫微斗数命盘 Service 接口
 *
 * @author JTWORLD
 */
public interface ZiweiChartService {

    /**
     * 计算并保存命盘
     *
     * @param reqVO 出生信息
     * @return 完整命盘
     */
    ZiweiChartRespVO calculateAndSave(ZiweiChartCreateReqVO reqVO);

    /**
     * 获取命盘详情
     */
    ZiweiChartRespVO getChart(Long id);

    /**
     * 分页查询
     */
    PageResult<ZiweiChartRespVO> getChartPage(ZiweiChartPageReqVO reqVO);

    /**
     * 删除命盘
     */
    void deleteChart(Long id);

}
