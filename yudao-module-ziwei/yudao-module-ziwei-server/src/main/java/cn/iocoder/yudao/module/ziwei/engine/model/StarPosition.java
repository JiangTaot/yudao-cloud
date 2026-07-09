package cn.iocoder.yudao.module.ziwei.engine.model;

import cn.iocoder.yudao.module.ziwei.enums.SihuaTypeEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarBrightnessEnum;
import cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 星曜落位（某颗星在某个宫位的具体状态）
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarPosition {

    /** 星曜编码 */
    private String starCode;

    /** 星曜名称 */
    private String starName;

    /** 星曜类型 */
    private StarTypeEnum starType;

    /** 亮度（庙旺得利平陷） */
    private StarBrightnessEnum brightness;

    /** 四化类型（如果是化星） */
    private SihuaTypeEnum sihuaType;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(starName != null ? starName : starCode);
        if (brightness != null) sb.append("[").append(brightness.getName()).append("]");
        if (sihuaType != null) sb.append("(").append(sihuaType.getShortName()).append(")");
        return sb.toString();
    }

}
