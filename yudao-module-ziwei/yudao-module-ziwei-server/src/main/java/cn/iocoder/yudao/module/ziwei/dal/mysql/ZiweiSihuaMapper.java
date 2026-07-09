package cn.iocoder.yudao.module.ziwei.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiSihuaDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ZiweiSihuaMapper extends BaseMapperX<ZiweiSihuaDO> {

    default List<ZiweiSihuaDO> selectListByChartId(Long chartId) {
        return selectList(new LambdaQueryWrapperX<ZiweiSihuaDO>()
                .eq(ZiweiSihuaDO::getChartId, chartId));
    }

    default void deleteByChartId(Long chartId) {
        delete(new LambdaQueryWrapperX<ZiweiSihuaDO>()
                .eq(ZiweiSihuaDO::getChartId, chartId));
    }

}
