package cn.iocoder.yudao.module.ziwei.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiPalaceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ZiweiPalaceMapper extends BaseMapperX<ZiweiPalaceDO> {

    default List<ZiweiPalaceDO> selectListByChartId(Long chartId) {
        return selectList(new LambdaQueryWrapperX<ZiweiPalaceDO>()
                .eq(ZiweiPalaceDO::getChartId, chartId)
                .orderByAsc(ZiweiPalaceDO::getPalaceType));
    }

    default void deleteByChartId(Long chartId) {
        delete(new LambdaQueryWrapperX<ZiweiPalaceDO>()
                .eq(ZiweiPalaceDO::getChartId, chartId));
    }

}
