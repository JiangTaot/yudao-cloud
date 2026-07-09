package cn.iocoder.yudao.module.ziwei.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ziwei_daxian")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiDaxianDO extends BaseDO {

    @TableId
    private Long id;
    private Long chartId;
    private Integer sequenceOrder;
    private Integer ageStart;
    private Integer ageEnd;
    private Integer calYearStart;
    private Integer calYearEnd;
    private Integer palaceType;
    private Boolean direction;

}
