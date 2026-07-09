package cn.iocoder.yudao.module.ziwei.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartPageReqVO;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiChartDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 命盘 Mapper
 *
 * @author JTWORLD
 */
@Mapper
public interface ZiweiChartMapper extends BaseMapperX<ZiweiChartDO> {

    default PageResult<ZiweiChartDO> selectPage(ZiweiChartPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZiweiChartDO>()
                .eqIfPresent(ZiweiChartDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZiweiChartDO::getGender, reqVO.getGender())
                .betweenIfPresent(ZiweiChartDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZiweiChartDO::getId));
    }

}
