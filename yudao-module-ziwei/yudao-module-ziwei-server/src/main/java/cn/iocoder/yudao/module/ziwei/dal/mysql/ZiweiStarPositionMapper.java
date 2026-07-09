package cn.iocoder.yudao.module.ziwei.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiStarPositionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ZiweiStarPositionMapper extends BaseMapperX<ZiweiStarPositionDO> {

    default List<ZiweiStarPositionDO> selectListByChartId(Long chartId) {
        return selectList(new LambdaQueryWrapperX<ZiweiStarPositionDO>()
                .eq(ZiweiStarPositionDO::getChartId, chartId)
                .orderByAsc(ZiweiStarPositionDO::getPalaceId, ZiweiStarPositionDO::getSortOrder));
    }

    default List<ZiweiStarPositionDO> selectListByPalaceId(Long palaceId) {
        return selectList(new LambdaQueryWrapperX<ZiweiStarPositionDO>()
                .eq(ZiweiStarPositionDO::getPalaceId, palaceId)
                .orderByAsc(ZiweiStarPositionDO::getSortOrder));
    }

    default void deleteByChartId(Long chartId) {
        delete(new LambdaQueryWrapperX<ZiweiStarPositionDO>()
                .eq(ZiweiStarPositionDO::getChartId, chartId));
    }

}
