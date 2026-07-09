package cn.iocoder.yudao.module.ziwei.engine.star;

import cn.iocoder.yudao.module.ziwei.engine.model.StarPosition;
import cn.iocoder.yudao.module.ziwei.enums.SihuaTypeEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 四化计算器（化禄、化权、化科、化忌）
 * <p>
 * 按生年天干确定四颗化星
 *
 * @author JTWORLD
 */
public class SihuaCalculator {

    /**
     * 根据年干计算四化
     *
     * @param yearGan 生年天干
     * @return 四个星曜落位对象（仅含星码和四化类型）
     */
    public List<StarPosition> calculateNatalSihua(TianGanEnum yearGan) {
        TianGanEnum.SihuaStars stars = yearGan.getSihuaStars();
        List<StarPosition> result = new ArrayList<>();

        result.add(createSihuaPosition(stars.huaLu(), SihuaTypeEnum.HUA_LU));
        result.add(createSihuaPosition(stars.huaQuan(), SihuaTypeEnum.HUA_QUAN));
        result.add(createSihuaPosition(stars.huaKe(), SihuaTypeEnum.HUA_KE));
        result.add(createSihuaPosition(stars.huaJi(), SihuaTypeEnum.HUA_JI));

        return result;
    }

    /**
     * 根据大限天干计算大限四化
     */
    public List<StarPosition> calculateDaxianSihua(TianGanEnum daxianTianGan) {
        return calculateNatalSihua(daxianTianGan); // 算法相同
    }

    /**
     * 根据流年天干计算流年四化
     */
    public List<StarPosition> calculateLiunianSihua(TianGanEnum liunianTianGan) {
        return calculateNatalSihua(liunianTianGan);
    }

    private StarPosition createSihuaPosition(String starCode, SihuaTypeEnum sihuaType) {
        String starName = getStarNameByCode(starCode);
        return StarPosition.builder()
                .starCode(starCode)
                .starName(starName)
                .sihuaType(sihuaType)
                .build();
    }

    private String getStarNameByCode(String code) {
        return switch (code) {
            case "lianzhen" -> "廉贞"; case "pojun" -> "破军";
            case "wuqu" -> "武曲"; case "taiyang" -> "太阳";
            case "tianji" -> "天机"; case "tianliang" -> "天梁";
            case "ziwei" -> "紫微"; case "taiyin" -> "太阴";
            case "tiantong" -> "天同"; case "wenchang" -> "文昌";
            case "jumen" -> "巨门"; case "tanlang" -> "贪狼";
            case "youbi" -> "右弼"; case "zuofu" -> "左辅";
            case "wenqu" -> "文曲";
            default -> code;
        };
    }

}
