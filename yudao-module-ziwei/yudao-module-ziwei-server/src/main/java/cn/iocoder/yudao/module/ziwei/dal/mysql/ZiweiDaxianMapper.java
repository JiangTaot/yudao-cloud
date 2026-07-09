package cn.iocoder.yudao.module.ziwei.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiDaxianDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ZiweiDaxianMapper extends BaseMapperX<ZiweiDaxianDO> {

    default List<ZiweiDaxianDO> selectListByChartId(Long chartId) {
        return selectList(new LambdaQueryWrapperX<ZiweiDaxianDO>()
                .eq(ZiweiDaxianDO::getChartId, chartId)
                .orderByAsc(ZiweiDaxianDO::getSequenceOrder));
    }

    default void deleteByChartId(Long chartId) {
        delete(new LambdaQueryWrapperX<ZiweiDaxianDO>()
                .eq(ZiweiDaxianDO::getChartId, chartId));
    }

}
