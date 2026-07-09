package cn.iocoder.yudao.module.ziwei.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ziwei_star_position")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiStarPositionDO extends BaseDO {

    @TableId
    private Long id;
    private Long chartId;
    private Long palaceId;
    private String starCode;
    private Integer starType;
    private Integer brightness;
    private Integer sihuaType;
    private Integer sortOrder;

}
