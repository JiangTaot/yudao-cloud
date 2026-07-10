package cn.iocoder.yudao.module.ziwei.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 命盘响应 VO
 *
 * @author JTWORLD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiChartRespVO {

    @Schema(description = "命盘ID")
    private Long id;

    // ========== 出生信息 ==========
    private Integer solarYear;
    private Integer solarMonth;
    private Integer solarDay;
    private Integer solarHour;
    private Integer solarMinute;
    private Integer gender;
    private String birthPlace;

    // ========== 八字 ==========
    private String yearPillar;
    private String monthPillar;
    private String dayPillar;
    private String hourPillar;

    // ========== 核心 ==========
    private String mingGong;
    private String shenGong;
    private String wuxingJu;

    // ========== 十二宫 ==========
    private List<PalaceVO> palaces;

    // ========== 四化 ==========
    private NatalSihuaVO natalSihua;

    // ========== 大限 ==========
    private List<DaxianVO> daxians;

    // ========== 格局 ==========
    private List<String> patterns;

    /** 格局详情列表 */
    private List<PatternDetailVO> patternDetails;

    private LocalDateTime createTime;

    // --- 内嵌类 ---

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PalaceVO {
        private Integer palaceType;
        private String palaceName;
        private String dizhi;
        private String tianGan;
        private Boolean isShenGong;
        private String daXianLabel;
        private List<StarVO> majorStars;
        private List<StarVO> auxiliaryStars;
        private List<StarVO> minorStars;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StarVO {
        private String starCode;
        private String starName;
        private String brightness;
        private String sihuaType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NatalSihuaVO {
        private StarVO huaLu;
        private StarVO huaQuan;
        private StarVO huaKe;
        private StarVO huaJi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DaxianVO {
        private Integer ageStart;
        private Integer ageEnd;
        private String palaceName;
        private String direction;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatternDetailVO {
        /** 格局名称 */
        private String name;
        /** 格局等级（上格/中格/平格/恶格） */
        private String level;
        /** 格局描述 */
        private String description;
        /** 古籍出处 */
        private String source;
        /** 必须满足的条件 */
        private List<String> required;
        /** 加分条件 */
        private List<String> bonus;
        /** 破格条件 */
        private List<String> breaking;
    }

}
