package cn.iocoder.yudao.module.ziwei.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ziwei_sihua")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiSihuaDO extends BaseDO {

    @TableId
    private Long id;
    private Long chartId;
    private Integer sihuaScope;
    private Integer refYear;
    private String huaLuStarCode;
    private String huaQuanStarCode;
    private String huaKeStarCode;
    private String huaJiStarCode;

}
