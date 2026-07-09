package cn.iocoder.yudao.module.ziwei.engine.model;

import cn.iocoder.yudao.module.ziwei.enums.DiZhiEnum;
import cn.iocoder.yudao.module.ziwei.enums.PalaceTypeEnum;
import cn.iocoder.yudao.module.ziwei.enums.TianGanEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宫位（包含该宫所有星曜落位信息）
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Palace {

    /** 宫位类型 */
    private PalaceTypeEnum type;

    /** 宫位所在地支 */
    private DiZhiEnum diZhi;

    /** 宫干（天干） */
    private TianGanEnum tianGan;

    /** 是否为身宫 */
    private boolean isShenGong;

    /** 大限起始岁数（null=非大限起始宫） */
    private Integer daXianStartAge;

    /** 大限结束岁数 */
    private Integer daXianEndAge;

    /** 该宫内的星曜落位列表 */
    @Builder.Default
    private List<StarPosition> stars = new ArrayList<>();

    // ========== 便利方法 ==========

    /**
     * 添加星曜
     */
    public void addStar(StarPosition star) {
        if (stars == null) stars = new ArrayList<>();
        // 避免重复添加同一星曜
        boolean exists = stars.stream().anyMatch(s -> s.getStarCode().equals(star.getStarCode()));
        if (!exists) {
            stars.add(star);
        }
    }

    /**
     * 获取所有主星
     */
    public List<StarPosition> getMajorStars() {
        return stars.stream()
                .filter(s -> s.getStarType() == cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum.MAJOR)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有辅星
     */
    public List<StarPosition> getAuxiliaryStars() {
        return stars.stream()
                .filter(s -> s.getStarType() == cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum.AUXILIARY)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有杂曜
     */
    public List<StarPosition> getMinorStars() {
        return stars.stream()
                .filter(s -> s.getStarType() == cn.iocoder.yudao.module.ziwei.enums.StarTypeEnum.MINOR)
                .collect(Collectors.toList());
    }

    /**
     * 获取该宫的四化星列表
     */
    public List<StarPosition> getSihuaStars() {
        return stars.stream()
                .filter(s -> s.getSihuaType() != null)
                .collect(Collectors.toList());
    }

    /**
     * 是否包含指定星曜编码
     */
    public boolean hasStar(String starCode) {
        return stars.stream().anyMatch(s -> s.getStarCode().equals(starCode));
    }

    /**
     * 大限年龄段显示标签
     */
    public String getDaXianLabel() {
        if (daXianStartAge != null && daXianEndAge != null) {
            return daXianStartAge + "-" + daXianEndAge + "岁";
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s(%s%s) stars=%d",
                type != null ? type.getName() : "?",
                diZhi != null ? diZhi.getName() : "?",
                tianGan != null ? tianGan.getName() : "?",
                stars != null ? stars.size() : 0);
    }

}
