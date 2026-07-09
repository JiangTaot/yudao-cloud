package cn.iocoder.yudao.module.ziwei.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ziwei_palace")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiPalaceDO extends BaseDO {

    @TableId
    private Long id;
    private Long chartId;
    private Integer palaceType;
    private String dizhi;
    private String tianGan;
    private Boolean isShenGong;
    private Integer daXianStartAge;
    private Integer daXianEndAge;

}
